(ns gilded-gauge.components
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [update-num format-number get-initials]]))


(defn stats [n net-worth]
  [:ul.stats
    (map-indexed
      (fn [i [k v]]
        [:li
          {:key i}
          [:em (format-number (Math.round (/ n v)))]
          (str " " k)])
      (assoc data/stats "times your net worth" net-worth))])


(defn preset-list [presets key]
  [:ul.preset-list
    (map
      (fn [[k v]]
        [:li {:key k :on-click #(update-num key v)} k])
      presets)])


(defn input [key val]
  [:input {:value      (format-number val)
           :on-change  #(update-num key %)
           :size       (inc (count (str val)))
           :type       "text"
           :max-length 10}])


(defn portrait [name]
  (let [src (get data/images name)]
    [:div.portrait
      (if src
        {:style {:backgroundImage (str "url(" src ")")}}
        [:div (get-initials name)])]))


(defn timeline-point [i max [year label]]
  (let [pct (* 100 (/ year max))]
    [:div.timeline-point
      {:style {:left   (str pct \%)
               :height (str (+ 5.5 (* i 1.8)) "rem")}}
      [:div.timeline-label
        (if (>= year 1e4) (format-number year) year)
        (when (zero? i) " A.D.")
        (when label
          (condp = i 1 "†" 3 "‡"))]
      [:div.timeline-tick]
      [:div.timeline-dot "•"]]))
