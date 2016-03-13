(ns gilded-gauge.state
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [parse-event]]
            [gilded-gauge.objects :refer [create-menagerie]]))

(defonce app
  (atom {:current-person     (rand-int (count data/ranked))
         :net-worth          50000
         :amount             100
         :show-person-select false
         :menagerie1         {}
         :menagerie2         {}}))


(defn toggle-person-select! []
  (swap! app update :show-person-select not))


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
    :menagerie2 (create-menagerie a2)))
