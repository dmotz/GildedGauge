(ns gilded-gauge.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :refer-macros [html]]
            [gilded-gauge.state :refer [app]]
            [gilded-gauge.data :as data]
            [gilded-gauge.presets :as presets]
            [gilded-gauge.components :refer [stats preset-list input]]
            [gilded-gauge.utils :refer [update-num calc-equiv get-initials
                                        format-number toggle-person-select
                                        select-person]]))

(enable-console-print!)

(om/root
  (fn [{:keys [current-person net-worth amount show-person-select]} _]
    (reify om/IRender
      (render [_]
        (let [[rich-name
               rich-worth] (nth data/ranked current-person)
               equiv       (calc-equiv rich-worth net-worth amount)]
          (html
            [:div
              [:h1#logo "Gilded Gauge"]

              [:div#columns
                [:div.column
                  [:span "If I have a net worth of "]
                  [:div.input-holder
                    [:span.input-wrap
                      (input :net-worth net-worth)]
                    [:div.range-slider (preset-list presets/worths :net-worth)]]

                  [:span ", then me spending "]

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
                  (stats equiv net-worth)]]

              [:div#timeline
                [:div#baseline]]])))))

  app
  {:target (. js/document (getElementById "app"))})
