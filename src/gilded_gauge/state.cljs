(ns gilded-gauge.state
  (:require [gilded-gauge.utils :refer [parse-event]]
            [gilded-gauge.objects :refer [create-menagerie]]))

(def preset-names #{"Jeff Bezos"
                    "Bill Gates"
                    "Warren Buffet"
                    "Mark Zuckerberg"
                    "Larry Page"
                    "Sergey Brin"
                    "Steve Ballmer"
                    "Carlos Slim"
                    "Larry Ellison"
                    "Charles Koch"
                    "Mukesh Ambani"
                    "Jim Walton"
                    "Alice Walton"
                    "Jack Ma"
                    "MacKenzie Bezos"
                    "Elon Musk"
                    "Sheldon Adelson"
                    "Phil Knight"
                    "Michael Dell"
                    "Laurene Powell Jobs"})


(defonce app
  (atom {:current-person     nil
         :net-worth          45000
         :amount             50
         :show-person-select false
         :show-about-view    false
         :menagerie1         {}
         :menagerie2         {}
         :iterations         0
         :ranking            nil}))


(defn fetch-ranking! []
  (->
   (js/fetch "ranking.json")
   (.then #(.json %))
   (.then js->clj)
   (.then #(swap! app assoc
                  :ranking (mapv
                            (fn [[name worth img]]
                              {:name name
                               :worth worth
                               :img (when img
                                      (str "https://upload.wikimedia.org/wikipedia/commons/" img))})
                            %)
                  :current-person (->>
                                   %
                                   (map vector (range))
                                   (filter (fn [[_ [name]]] (preset-names name)))
                                   (map first)
                                   rand-nth)))))

(defn toggle-person-select! []
  (swap! app update :show-person-select not))


(defn hide-person-select! []
  (swap! app assoc :show-person-select false))


(defn toggle-about-view! []
  (swap! app update :show-about-view not))

(defn hide-about-view! []
  (swap! app assoc :show-about-view false))

(defn select-person! [i]
  (swap! app assoc :current-person i :show-person-select false))


(defn update-num! [k e]
  (when-let [v (parse-event e)]
    (let [a @app]
      (when (case k
              :amount    (<= v (:net-worth a))
              :net-worth (>= v (:amount a))
              true)
        (swap! app assoc k v)))))


(defn update-menageries! [a1 a2]
  (swap!
   app
   assoc
   :menagerie1 (create-menagerie a1)
   :menagerie2 (create-menagerie a2))
  (swap! app update :iterations inc))
