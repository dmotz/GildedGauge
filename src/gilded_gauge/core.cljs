(ns gilded-gauge.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :refer-macros [html]]
            [gilded-gauge.state :refer [app]]
            [gilded-gauge.data :as data]
            [gilded-gauge.presets :as presets]
            [gilded-gauge.components :refer [stats preset-list input
                                             timeline-point portrait]]
            [gilded-gauge.utils :refer [update-num calc-equiv get-initials
                                        format-number toggle-person-select
                                        select-person calc-year-paid]]))

(enable-console-print!)

(om/root
  (fn [{:keys [current-person net-worth amount show-person-select]} _]
    (reify
      om/IRender
      (render [_]
        (let [rich-map    (nth data/ranked current-person)
              rich-name   (:name rich-map)
              rich-worth  (:worth rich-map)
              equiv       (calc-equiv rich-worth net-worth amount)
              $equiv      (str \$ (format-number equiv))]
          (html
            [:div
              [:div#person-list
                {:class-name (when show-person-select "active")}
                [:div.x {:on-click toggle-person-select} \×]
                [:ul
                  (map-indexed
                    (fn [i m]
                      [:li
                        {:key i :on-click #(select-person i)}
                        (portrait m)
                        [:div (:name m)]])
                    data/ranked)]]

              [:h1#logo "Gilded Gauge"]

              [:div#columns
                [:div.column
                  [:span "If I have a net worth of "]
                  [:div.input-holder
                    [:span.input-wrap
                      (input :net-worth net-worth)]
                    [:div.range-slider
                      (preset-list presets/worths :net-worth)]
                    [:span ","]]

                  [:span " then me spending "]

                  [:div.input-holder
                    [:span.input-wrap
                      (input :amount amount)]
                    [:div.range-slider
                      [:input
                        {:type      "range"
                         :value     amount
                         :on-change #(update-num :amount %)
                         :min       0
                         :max       net-worth
                         :step      "10"}]
                      (preset-list presets/amounts :amount)]]

                  [:span " is the equivalent of…"]]

                [:div.column
                  [:div#main-portrait
                    {:on-click   toggle-person-select}
                    (portrait rich-map)]

                  [:span#current-person
                    {:on-click toggle-person-select}
                    rich-name]

                  [:span "Put another way:"]
                  (stats equiv net-worth)]]

              (let [year-paid (calc-year-paid equiv data/median-global-income)
                    events    (assoc
                                presets/dates
                                year-paid
                                (str
                                  "‡ year to have earned "
                                  $equiv
                                  " on global median income"))]
                [:footer
                  [:div#timeline
                    [:div#baseline]
                    (map-indexed
                      (fn [i t] (timeline-point i year-paid t))
                      events)]

                  [:div#legend
                    [:ul
                      (map
                        (fn [[_ label]] (when label [:li label]))
                        events)]]])])))))
  app
  {:target (. js/document (getElementById "app"))})
