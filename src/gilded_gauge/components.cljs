(ns gilded-gauge.components
  (:require [clojure.string :as str]
            [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [format-number get-initials inflect]]
            [gilded-gauge.state :refer [update-num!]]
            [gilded-gauge.objects :refer [objects]]))


(defn stats [n net-worth]
  [:ul.stats
    (map-indexed
      (fn [i [k v]]
        (let [words (str/split k #" ")]
          [:li
            {:key i}
            (let [ratio (/ n v)]
              [:em
                (str
                  (if (< ratio 1)
                    (.toFixed ratio 2)
                    (format-number (Math.round ratio)))
                  " "
                  (first words))])
            (str " " (apply str (interpose " " (rest words))))]))
      (assoc data/stats "times your net worth" net-worth))])


(defn preset-list [presets key]
  [:ul.preset-list
    (map
      (fn [[k v]]
        [:li {:key k :on-click #(update-num! key v)} k])
      presets)])


(defn input [key val]
  [:input {:value      (format-number val)
           :on-change  #(update-num! key %)
           :size       (inc (count (str val)))
           :type       "text"
           :max-length 10}])


(defn portrait [{:keys [name img]}]
  [:div.portrait
    (if img
      {:style {:backgroundImage (str "url(" img ")")}}
      [:div (get-initials name)])])


(defn timeline-point [i max [year label]]
  (let [pct (* 100 (/ year max))]
    [:div.timeline-point
      {:key   i
       :style {:left   (str pct \%)
               :height (str (+ 5.5 (* i 1.8)) "rem")}}
      [:div.timeline-label
        (if (>= year 1e4) (format-number year) year)
        (when (zero? i) " A.D.")
        (when label
          (condp = i 1 "†" 3 "‡"))]
      [:div.timeline-tick]
      [:div.timeline-dot "•"]]))


(defn menagerie-list [menagerie]
  (interpose
    " / "
    (map
      (fn [[k n]]
        (let [obj (k objects)]
          [:span.menagerie-item
            [:span.menagerie-name (str n " " (inflect (first obj) n))]
            [:span.menagerie-price (str \$ (format-number (* n (second obj))))]]))
      menagerie)))
