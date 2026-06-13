(ns scrape
  (:require ["puppeteer$default" :as puppeteer]
            ["cheerio" :as cheerio]
            ["fs" :as fs]
            [promesa.core :as p]
            [clojure.string :as str]))

(def output-path "resources/public/ranking.json")
(def ranking-url "https://www.bloomberg.com/billionaires/")
(def ranking-url2 "https://www.forbes.com/profile/michael-bloomberg/")
(def wiki-url "https://en.wikipedia.org/wiki/")
(def thumb-prefix "//upload.wikimedia.org/wikipedia/commons/")
(def user-agent (str "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                     "AppleWebKit/537.36 (KHTML, like Gecko) "
                     "Chrome/125.0.0.0 Safari/537.36"))
(def ranking-headers
  #js {"user-agent" user-agent
       "accept" (str "text/html,application/xhtml+xml,application/xml;"
                     "q=0.9,image/avif,image/webp,image/apng,*/*;"
                     "q=0.8,application/signed-exchange;v=b3;q=0.7")
       "cookie" (-> (.readFileSync fs "./scripts/biscuit" "utf8") str/trim)})
(def take-n 50)
(def goto-options #js {:waitUntil "domcontentloaded"
                       :timeout 60000})
(defn browser-launch-options [browser-home-dir browser-profile-dir]
  #js {:headless "new"
       :env #js {"HOME" browser-home-dir}
       :userDataDir browser-profile-dir
       :args #js ["--disable-crash-reporter"
                  "--disable-crashpad"
                  "--no-sandbox"]})

(def aliases
  {"Ken Griffin" "Kenneth C. Griffin"
   "German Larrea" "German Larrea Mota-Velasco"
   "John Mars" "John Franklyn Mars"
   "Rob Walton" "S. Robson Walton"})

(defn str->float [s]
  (when-not (string? s)
    (throw (js/Error. (str "Expected string, got: " (type s)))))
  (let [amount (some-> (re-find #"[\d,.]+" s)
                       (str/replace #"," "")
                       js/parseFloat)
        unit (re-find #"[TMB]" s)]
    (case unit
      "T" (* amount 1000)
      "M" (/ amount 1000)
      amount)))

(defn prepare-ranking-page [page]
  (p/let [_ (.setExtraHTTPHeaders page ranking-headers)
          _ (.setUserAgent page user-agent)]
    page))

(defn parse-bloomberg-page [content]
  (let [$ (cheerio/load content)]
    (->> (.get ($ ".table-row"))
         (take take-n)
         (map (fn [el]
                (let [name (-> ($ ".t-name a" el)
                              (.first)
                              (.text)
                              str/trim
                              (str/replace #"(\w )(\w )(\w)" "$1$3")
                              (str/replace #" & family$" ""))
                      worth (-> ($ ".t-nw" el)
                               (.first)
                               (.text)
                               str->float)]
                  {:name name :worth worth}))))))

(defn get-image [browser {:keys [name] :as person}]
  (p/let [page (.newPage browser)]
    (-> (p/let [wiki-name (str/replace (get aliases name name) #"\s" "_")
                _ (.goto page (str wiki-url wiki-name) goto-options)
                img-src (.evaluate page (js/eval "
            () => {
              const img = document.querySelector('.infobox img');
              return img ? img.src : null;
            }
          "))
                img (when img-src
                      (-> img-src
                          (str/replace #"^https:" "")
                          (str/replace #"^//upload\.wikimedia\.org/wikipedia/commons/" "")))]
          (assoc person :img img))
        (.finally (fn [] (.close page))))))

(defn get-images [browser people]
  (reduce (fn [acc-p person]
            (p/let [acc acc-p
                    with-image (.catch (get-image browser person) (fn [_] person))]
              (conj acc with-image)))
          (p/resolved [])
          people))

(defn scrape []
  (let [browser-home-dir (.mkdtempSync fs "/tmp/gilded-gauge-browser-home-")
        browser-profile-dir (.mkdtempSync fs "/tmp/gilded-gauge-puppeteer-")]
    (.mkdirSync fs browser-home-dir #js {:recursive true})
    (.mkdirSync fs browser-profile-dir #js {:recursive true})
    (p/let [browser (.launch puppeteer (browser-launch-options browser-home-dir browser-profile-dir))]
      (.finally
       (p/let [page (.newPage browser)
               _ (prepare-ranking-page page)
               _ (.goto page ranking-url goto-options)
               content (.content page)
               bloomberg-data (parse-bloomberg-page content)

               _ (.goto page ranking-url2 goto-options)
               raw-worth (.evaluate page (js/eval "
            () => {
              const worth = document.querySelector('.profile-info__item-value');
              return worth ? worth.textContent : null;
            }
          "))
               bloomberg-worth (str->float raw-worth)

               all-data (conj bloomberg-data
                              {:name "Michael Bloomberg"
                               :worth bloomberg-worth})

               data-with-images (get-images browser all-data)

               final-data (->> data-with-images
                               (sort-by :worth >)
                               (take take-n)
                               (map (fn [{:keys [name worth img]}]
                                      (if img
                                        [name worth img]
                                        [name worth])))
                               vec)
               _ (.close page)]

         (if (>= (count final-data) take-n)
           (.writeFileSync fs output-path (js/JSON.stringify (clj->js final-data)))
           (throw (js/Error. (str "Expected at least " take-n " rows, got "
                                   (count final-data))))))
       (fn [] (.close browser))))))

(scrape)
