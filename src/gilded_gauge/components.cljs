(ns gilded-gauge.components
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [update-num format-number]]))


(defn stats [n net-worth]
  [:ul.stats
    (map-indexed
      (fn [i [k v]]
        [:li
          {:key i}
          [:em (format-number (Math.round (/ n v)))]
          (str " " k)])
      (assoc data/stats "times your net worth" net-worth))])


(defn preset-list [presets current-amount]
  [:ul.preset-list
    (map
      (fn [[k v]]
        (when (not= v current-amount)
          [:li
            {:key k :on-click #(update-num :amount v)}
            (str \$ (format-number v))]))
      presets)])


(defn input [key val]
  [:input {:value      (format-number val)
           :on-change  #(update-num key %)
           :size       (inc (count (str val)))
           :type       "text"
           :max-length 10}])
