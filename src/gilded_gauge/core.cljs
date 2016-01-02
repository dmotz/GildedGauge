(ns gilded-gauge.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [gilded-gauge.data :as data]))

(enable-console-print!)

(defonce app-state
  (atom {:current-person     (rand-int (count data/ranked))
         :net-worth          40000
         :amount             100
         :show-person-select false}))

(def net-worth-min 5)
(def net-worth-max 5e5)

(defn update-num [k e]
  (let [v (if (number? e)
            e
            (-> e
                (.. -target -value)
                (str/replace #"\D" "")
                (js/parseInt 10)))]
    (when-not (js/isNaN v)
      (swap! app-state assoc k v))))


(defn calc-equiv [rich-worth net-worth amount]
  (Math/round (* amount (/ (* rich-worth 1e9) net-worth))))


(defn get-initials [name]
  (let [xs (str/split name #" ")]
    (str (ffirst xs) (first (last xs)))))


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


(defn toggle-person-select []
  (swap! app-state update :show-person-select not))


(defn select-person [i]
  (swap! app-state assoc :current-person i :show-person-select false))


(def amount-presets (->> [1 10 50 100 500 1000 5000]
                         repeat
                         (take 2)
                         (apply zipmap)))


(def worth-presets
  {"median U.S. household income"   data/median-us-income
   "median global household income" data/median-global-income})


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
                  (preset-list worth-presets net-worth)))
              (dom/span nil ", then me spending ")
              #_
              (dom/input
                #js {:type     "range"
                     :value    net-worth
                     :onChange #(update-num :net-worth %)
                     :min      net-worth-min
                     :max      (max net-worth-max net-worth)})
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
                  (preset-list amount-presets amount)))
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

  app-state
  {:target (. js/document (getElementById "app"))})
