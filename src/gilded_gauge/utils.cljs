(ns gilded-gauge.utils
  (:require [clojure.string :as str]))


(defn set-timeout! [ms f]
  (js/setTimeout f ms))


(defn kill-timeout! [n]
  (js/clearTimeout n))


(defn parse-event [e]
  (let [v (if (number? e)
            e
            (-> e
                (.. -target -value)
                (str/replace #"\D" "")
                (js/parseInt 10)))]
    (if (js/isNaN v) nil v)))


(defn calc-equiv [rich-worth net-worth amount]
  (Math/round (* amount (/ (* rich-worth 1e9) net-worth))))


(def get-initials
  (memoize
   (fn [name]
     (let [xs        (str/split name #" ")
           firstname (first xs)
           surname   (last xs)]
       (str
        (if (= firstname "Prince")
          (first (second xs))
          (ffirst xs))
        (first
         (if (= surname "Jr.")
           (last (butlast xs))
           surname)))))))


(defn format-number [n]
  (if (< n 1)
    (.toFixed n 2)
    (-> n
        Math/round
        str
        reverse
        (->> (apply str))
        (str/replace #"(\d{3})" "$1,")
        reverse
        (->> (drop-while #(= \, %)))
        (->> (apply str)))))


(def year-now (.getFullYear (js/Date.)))
(defn calc-year-paid [goal income]
  (js/Math.round (+ year-now (/ goal income))))


(defn inflect [s n]
  (str/replace s #"\?" (if (= 1 n) "" "s")))
