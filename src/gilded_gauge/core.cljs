(ns gilded-gauge.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :refer-macros [html]]
            [gilded-gauge.state :refer [app]]
            [gilded-gauge.data :as data]
            [gilded-gauge.presets :as presets]
            [gilded-gauge.utils :refer [update-num calc-equiv get-initials
                                        format-number toggle-person-select
                                        select-person]]))


(enable-console-print!)

(defn stats-component [n net-worth]
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


(om/root
  (fn [{:keys [current-person net-worth amount show-person-select]} _]
    (reify om/IRender
      (render [_]
        (let [[rich-name rich-worth] (nth data/ranked current-person)
               equiv                 (calc-equiv rich-worth net-worth amount)]
          (html
            [:div
              [:h1 {:id "logo"} "Gilded Gauge"]

              [:div.column
                [:span "If I have a net worth of "]
                [:div.input-holder
                  [:span.input-wrap
                    [:input
                      {:type       "text"
                       :id         "net-worth"
                       :value      (format-number net-worth)
                       :on-change  #(update-num :net-worth %)
                       :size       (inc (count (str net-worth)))
                       :max-length "10"}]]
                  [:div.range-slider (preset-list presets/worths net-worth)]]

                [:span ", then me spending "]

                [:div.input-holder
                  [:span.input-wrap
                    [:input
                      {:id         "amount"
                       :type       "text"
                       :value      (format-number amount)
                       :on-change  #(update-num :amount %)
                       :size       (inc (count (str amount)))
                       :max-length "10"}]]
                  [:div.range-slider
                    [:input
                      {:type      "range"
                       :value     amount
                       :on-change #(update-num :amount %)
                       :min       0
                       :max       net-worth
                       :step      "10"}]
                    (preset-list presets/amounts amount)]]

                [:span "is the equivalent of…"]]

              [:div.column
                (let [src (get data/images rich-name)]
                  [:div#portrait
                    {:style    (when src {:backgroundImage (str "url(" src ")")})
                     :on-click toggle-person-select}
                    (when-not src [:div (get-initials rich-name)])])

                [:span#current-person
                  {:on-click toggle-person-select}
                  (first (nth data/ranked current-person))]

                (when show-person-select
                  [:ul#person-list
                    (map-indexed
                      (fn [i [name]]
                        (when (not= i current-person)
                          [:li {:key i :on-click #(select-person i)} name]))
                      data/ranked)])

                [:span " spending "]
                [:span.amount (str "$" (format-number equiv))]
                [:span "."]
                [:br]
                [:br]
                [:span "Put another way:"]
                (stats-component equiv net-worth)]])))))

  app
  {:target (. js/document (getElementById "app"))})
