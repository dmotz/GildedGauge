(ns gilded-gauge.scrape
  (:require [net.cgrand.enlive-html :refer [html-resource select]]
            [clojure.core.async :refer [go <!!]]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]))


(def output-path  "src/gilded_gauge/rankings.cljs")
(def ranking-url  "https://www.bloomberg.com/billionaires/")
(def wiki-url     "https://en.wikipedia.org/wiki/")
(def thumb-prefix "//upload.wikimedia.org/wikipedia/commons/thumb/")

(def headers {"user-agent"      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36"
              "accept"          "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
              "accept-language" "en-US,en;q=0.9"
              "cache-control"   "max-age=0"})

(def prefix-n (count thumb-prefix))
(def take-n   50)


(defn add-headers [req]
  (dorun (map (fn [[k v]] (.setRequestProperty req k v)) headers))
  req)


(defn parse-ranking [url]
  (with-open
   [stream (->> url
                java.net.URL.
                .openConnection
                add-headers
                .getContent)]
    (->
     stream
     html-resource
     (select [:.table-row])
     (->>
      (take take-n)
      (map #(do {:name  (->
                         %
                         (select [:.t-name :a])
                         first
                         text
                         str/trim
                         (str/replace #"(\w )(\w )(\w)" "$1$3"))

                 :worth (->
                         %
                         (select [:.t-nw])
                         first
                         text
                         (->>
                          str/trim
                          (drop 1)
                          butlast
                          (apply str)
                          Float/parseFloat))}))))))


(defn get-image [m]
  (go
    (try
      (assoc
       m
       :img
       (some->
        (str wiki-url (str/replace (:name m) #"\s" "_"))
        java.net.URL.
        html-resource
        (select [:.biography :img])
        first
        :attrs
        :src
        (->>
         (drop prefix-n)
         (apply str))))
      (catch Exception _
        m))))


(defn drop-nil-img [v]
  (let [img (nth v 2)]
    (if (or (nil? img) (.contains img ".svg"))
      (pop v)
      v)))

(with-open [w (-> output-path io/file io/writer)]
  (binding [*out* w]
    (let [rankings
          (->>
           (parse-ranking ranking-url)
           (map get-image)
           doall
           (map <!!)
           (map (juxt :name :worth :img))
           (map drop-nil-img)
           vec)]
      (pprint `(~'ns gilded-gauge.rankings "Autogenerated namespace"))
      (pprint `(def ~'rankings ~rankings)))))
