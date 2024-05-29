(ns scrape
  (:require [net.cgrand.enlive-html :refer [html-resource select text]]
            [clojure.core.async :refer [go <!!]]
            [clojure.string :refer [replace trim]]
            [clojure.data.json :as json])
  (:import  (java.net HttpURLConnection))
  (:refer-clojure :exclude [replace]))


(def output-path  "resources/public/ranking.json")
(def ranking-url  "https://www.bloomberg.com/billionaires/")
(def ranking-url2 "https://www.forbes.com/profile/michael-bloomberg/")
(def wiki-url     "https://en.wikipedia.org/wiki/")
(def thumb-prefix "//upload.wikimedia.org/wikipedia/commons/")
(def aliases
  {"Ken Griffin"   "Kenneth C. Griffin"
   "German Larrea" "German Larrea Mota-Velasco"
   "John Mars"     "John Franklyn Mars"
   "Rob Walton"    "S. Robson Walton"})

(def headers {"user-agent" (str
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                            "AppleWebKit/537.36 (KHTML, like Gecko) "
                            "Chrome/125.0.0.0 Safari/537.36")
              "accept"     (str
                            "text/html,application/xhtml+xml,application/xml;"
                            "q=0.9,image/avif,image/webp,image/apng,*/*;"
                            "q=0.8,application/signed-exchange;v=b3;q=0.7")
              "cookie"     (-> "./scripts/biscuit" slurp trim)})

(def prefix-n (count thumb-prefix))
(def take-n   50)


(defn add-headers [req]
  (dorun (map (fn [[k v]]
                (.setRequestProperty ^HttpURLConnection req k v)) headers))
  req)

(defn str->float [s]
  (->> s trim (drop 1) butlast (apply str) Float/parseFloat))


(defn parse-ranking [url1 url2]
  (conj
   (with-open
    [stream (->> url1
                 java.net.URL.
                 .openConnection
                 add-headers
                 (#(.getContent ^HttpURLConnection %)))]
     (->
      stream
      html-resource
      (select [:.table-row])
      (->>
       (take take-n)
       (map #(do {:name  (->
                          (select % [:.t-name :a])
                          first
                          text
                          trim
                          (replace #"(\w )(\w )(\w)" "$1$3")
                          (replace #" & family$" ""))

                  :worth (->>
                          (select % [:.t-nw])
                          first
                          text
                          str->float)})))))

   {:name  "Michael Bloomberg"
    :worth (->
            url2
            java.net.URL.
            html-resource
            (select [:.profile-info__item-value])
            first
            text
            str->float)}))


(defn get-image [m]
  (let [name (:name m)]
    (go
      (try
        (assoc
         m
         :img
         (some->
          (str wiki-url (replace (get aliases name name) #"\s" "_"))
          java.net.URL.
          html-resource
          (select [:.infobox :img])
          first
          :attrs
          :src
          (->>
           (drop prefix-n)
           (apply str))))
        (catch Exception _
          m)))))


(defn drop-nil-img [v]
  (let [img (nth v 2)]
    (if (or (nil? img)
            (.contains img ".svg")
            (.contains img "signature"))
      (pop v)
      v)))


(let [data (->>
            (parse-ranking ranking-url ranking-url2)
            (map get-image)
            doall
            (map <!!)
            (sort-by :worth)
            reverse
            (take take-n)
            (map (juxt :name :worth :img))
            (map drop-nil-img)
            vec)]

  (if (>= (count data) take-n)
    (spit output-path (json/write-str data))
    (println "No data updates found!")))
