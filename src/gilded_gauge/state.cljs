(ns gilded-gauge.state
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [parse-event]]
            [gilded-gauge.objects :refer [create-menagerie]]))

(def preset-indices [0 1 2 4 6 7 8 9 10 11 14 15 17 18 19 28 32 34 35])

(defonce app
  (atom {:current-person     (rand-nth preset-indices)
         :net-worth          45000
         :amount             50
         :show-person-select false
         :show-about-view    false
         :menagerie1         {}
         :menagerie2         {}
         :iterations         0}))


(defn toggle-person-select! []
  (swap! app update :show-person-select not))


(defn toggle-about-view! []
  (swap! app update :show-about-view not))


(defn select-person! [i]
  (swap! app assoc :current-person i :show-person-select false))


(defn update-num! [k e]
  (if-let [v (parse-event e)]
    (when (case k
            :amount    (<= v (:net-worth @app))
            :net-worth (>= v (:amount @app))
            true)
      (swap! app assoc k v))))


(defn update-menageries! [a1 a2]
  (swap!
    app
    assoc
    :menagerie1 (create-menagerie a1)
    :menagerie2 (create-menagerie a2))
  (swap! app update :iterations inc))
