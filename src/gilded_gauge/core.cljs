(ns gilded-gauge.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :refer-macros [html]]
            [gilded-gauge.state :refer [app]]
            [gilded-gauge.data :as data]
            [gilded-gauge.presets :as presets]
            [gilded-gauge.components :refer [stats preset-list input
                                             timeline-point portrait
                                             menagerie-list]]
            [gilded-gauge.utils :refer [calc-equiv get-initials format-number
                                        calc-year-paid set-timeout! kill-timeout!]]
            [gilded-gauge.state :refer [update-num! toggle-person-select!
                                        select-person! update-menageries!
                                        toggle-about-view!]]
            [gilded-gauge.emoji :as emoji]))

(enable-console-print!)

(def throttle-ms    100)
(def timeout        (atom))
(def engine-left    (atom))
(def engine-right   (atom))
(def resize-timeout (atom))

(defn rain! [amount equiv]
  (kill-timeout! @timeout)
  (reset!
    timeout
    (set-timeout! throttle-ms #(update-menageries! amount equiv))))


(om/root
  (fn [{:keys [current-person net-worth amount show-person-select
               show-about-view menagerie1 menagerie2] :as props} owner]

    (let [rich-map   (nth data/ranked current-person)
          rich-name  (:name rich-map)
          rich-worth (:worth rich-map)
          equiv      (calc-equiv rich-worth net-worth amount)
          $equiv     (str \$ (format-number equiv))]

      (reify
        om/IDidMount
        (did-mount [_]
          (let [w     (/ (.-innerWidth js/window) 2)
                h     (.-innerHeight js/window)
                left  (emoji/init (om/get-node owner "canvas-left") w h)
                right (emoji/init (om/get-node owner "canvas-right") w h)]

            (reset! engine-left left)
            (reset! engine-right right)
            (.addEventListener
              js/window
              "resize"
              (fn []
                (kill-timeout! @resize-timeout)
                (reset! resize-timeout (set-timeout! 100 #(emoji/resize! left right))))
              false)
            (rain! amount equiv)))

        om/IDidUpdate
        (did-update [_ prev-props _]
          (if (some #(not= (% prev-props) (% props)) [:net-worth :amount :current-person])
            (rain! amount equiv)
            (when (or
                    (not= (:menagerie1 prev-props) (:menagerie1 props))
                    (not= (:menagerie2 prev-props) (:menagerie2 props)))
              (emoji/run @engine-left menagerie1)
              (emoji/run @engine-right menagerie2))))

        om/IRender
        (render [_]
          (html
            [:div
              [:div#canvas-left.canvas {:ref "canvas-left"}]
              [:div#canvas-right.canvas {:ref "canvas-right"}]
              [:div#person-list
                {:class-name (when show-person-select "active")}
                [:div.x {:on-click toggle-person-select!} \×]
                [:ul
                  (map-indexed
                    (fn [i m]
                      [:li
                        {:key i :on-click #(select-person! i)}
                        (when (:img m) (portrait m))
                        [:div (:name m)]])
                    data/ranked)]]

              [:div#about-view
                {:class-name (when show-about-view "active")}
                [:div.x {:on-click toggle-about-view!} \×]
                [:div.content
                  [:p
                    "Gilded Gauge was built by "
                    [:a
                      {:href "http://oxism.com" :tab-index -1}
                      "Dan Motzenbecker"]
                    " and is "
                    [:a
                      {:href "https://github.com/dmotz/gilded-gauge" :tab-index -1}
                      "open source"]
                    "."]]]

              [:h1#logo
                {:on-click toggle-about-view!}
                "Gilded Gauge."]

              (let [year-paid (calc-year-paid equiv data/median-global-income)]
                [:div#timeline
                  [:div#baseline]
                  (map-indexed
                    (fn [i t] (timeline-point i year-paid t))
                    (assoc
                      presets/dates
                      year-paid
                      (str
                        "year to have earned "
                        $equiv
                        " on global median income")))])

              [:div#columns
                [:div#column-left.column
                  [:span "I have a net worth of "]
                  [:div.input-holder
                    [:span.input-wrap
                      (input :net-worth net-worth)]
                    [:div.range-slider
                      (preset-list presets/worths :net-worth)]
                    [:span "."]]

                  [:br]
                  [:span "When I spend "]
                  [:div.input-holder
                    [:span.input-wrap
                      (input :amount amount)]

                    [:div.range-slider
                      (preset-list presets/amounts :amount)]]

                  [:span "…"]
                  (menagerie-list menagerie1)]

                [:div#column-right.column
                  [:div#main-portrait
                    {:on-click toggle-person-select!}
                    (portrait rich-map)]

                  [:div#comparative-header
                    [:span "That’s the equivalent of "]
                    [:span#current-person
                      {:on-click toggle-person-select!}
                      rich-name]

                    [:span " spending "]
                    [:span.amount $equiv]
                    [:span "."]]

                  (stats equiv net-worth)
                  (menagerie-list menagerie2)]]])))))
  app
  {:target (.getElementById js/document "app")})
