(ns gilded-gauge.utils
  (:require [clojure.string :as str]
            [gilded-gauge.state :refer [app]]))


(defn update-num [k e]
  (let [v (if (number? e)
            e
            (-> e
                (.. -target -value)
                (str/replace #"\D" "")
                (js/parseInt 10)))]
    (when-not (js/isNaN v)
      (swap! app assoc k v))))


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
            (if (= surname "Jr.") (last (butlast xs)) surname)))))))


(defn format-number [n]
  (-> n
      Math/round
      str
      reverse
      (->> (apply str))
      (str/replace #"(\d{3})" "$1,")
      reverse
      (->> (drop-while #(= \, %)))
      (->> (apply str))))


(defn toggle-person-select []
  (swap! app update :show-person-select not))


(defn select-person [i]
  (swap! app assoc :current-person i :show-person-select false))


(def year-now (.getFullYear (js/Date.)))
(defn calc-year-paid [goal income]
  (Math.round (+ year-now (/ goal income))))


(defn inflect [s n]
  (str/replace s #"\?" (if (= 1 n) "" "s")))
