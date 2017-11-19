(ns gilded-gauge.presets
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [format-number]]))


(def $format (comp (partial str \$) format-number))

(defn $zip [xs]
  (zipmap xs (map $format xs)))


(def amounts (into (sorted-map) ($zip [1 5 10 20 50 100 500 1000 5000])))


(def worths
  (into
    (sorted-map)
    (merge
      {45000                     "average U.S. net worth"
       data/median-us-income     "median U.S. household income"
       data/median-global-income "median global household income"}
      ($zip [1e4 5e4 1e5 25e4 5e5 1e6 25e5 5e6]))))


(def net-worth-min 5)
(def net-worth-max 5e5)
(def present (.getFullYear (js/Date.) nil))

(def dates
  {1       nil
   476     "Fall of Rome"
   present nil})
