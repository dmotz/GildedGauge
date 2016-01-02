(ns gilded-gauge.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [gilded-gauge.state :refer [app]]
            [gilded-gauge.data :as data]
            [gilded-gauge.presets :as presets]
            [gilded-gauge.utils :refer [update-num calc-equiv get-initials
                                        format-number toggle-person-select
                                        select-person]]))


(enable-console-print!)

(defn stats-component [n net-worth]
  (dom/ul
    #js {:className "stats"}
    (map-indexed
      (fn [i [k v]]
        (dom/li
          #js {:key i}
          (dom/em nil (format-number (Math.round (/ n v))))
          (str " " k)))
      (assoc data/stats "times your net worth" net-worth))))


(defn preset-list [presets current-amount]
  (dom/ul
    #js {:className "preset-list"}
    (map
      (fn [[k v]]
        (when (not= v current-amount)
          (dom/li
            #js {:key     k
                 :onClick #(update-num :amount v)}
            (str \$ (format-number v)))))
      presets)))


(om/root
  (fn [{:keys [current-person net-worth amount show-person-select]} _]
    (reify om/IRender
      (render [_]
        (let [[rich-name rich-worth] (nth data/ranked current-person)
               equiv                 (calc-equiv rich-worth net-worth amount)]
          (dom/div nil
            (dom/h1 #js {:id "logo"} "Gilded Gauge")
            (dom/div
              #js {:className "column"}
              (dom/span nil "If I have a net worth of ")
              (dom/div
                #js {:className "input-holder"}
                (dom/span
                  #js {:className "input-wrap"}
                  (dom/input
                    #js {:type      "text"
                         :id        "net-worth"
                         :value     (format-number net-worth)
                         :onChange  #(update-num :net-worth %)
                         :size      (inc (count (str net-worth)))
                         :maxLength "10"}))
                (dom/div
                  #js {:className "range-slider"}
                  (preset-list presets/worths net-worth)))
              (dom/span nil ", then me spending ")
              (dom/div #js {:className "input-holder"}
                (dom/span
                  #js {:className "input-wrap"}
                  (dom/input
                    #js {:id        "amount"
                         :type      "text"
                         :value     (format-number amount)
                         :onChange  #(update-num :amount %)
                         :min       1
                         :size      (inc (count (str amount)))
                         :maxLength "10"}))
                (dom/div
                  #js {:className "range-slider"}
                  (dom/input
                    #js {:type     "range"
                         :value    amount
                         :onChange #(update-num :amount %)
                         :min      0
                         :max      net-worth
                         :step     "10"})
                  (preset-list presets/amounts amount)))
              (dom/span nil "is the equivalent of…"))
            (dom/div
              #js {:className "column"}
              (let [src (get data/images rich-name)]
                (dom/div
                 #js {:id      "portrait"
                      :style   (when src
                                 #js {:backgroundImage (str "url(" src ")")})
                      :onClick toggle-person-select}
                 (when-not src
                   (dom/div nil (get-initials rich-name)))))
              (dom/span
                #js {:id      "current-person"
                     :onClick toggle-person-select}
                (first (nth data/ranked current-person)))

              (when show-person-select
                (dom/ul
                  #js {:id "person-list"}
                  (map-indexed
                    (fn [i [name]]
                      (when (not= i current-person)
                        (dom/li
                          #js {:onClick #(select-person i)
                               :key     i}
                          name)))
                    data/ranked)))
              (dom/span nil " spending ")
              (dom/span
                #js {:className "amount"}
                (str "$" (format-number equiv)))
              (dom/span nil ".")
              (dom/br nil)
              (dom/br nil)
              (dom/span nil "Put another way:")
              (stats-component equiv net-worth)))))))

  app
  {:target (. js/document (getElementById "app"))})
