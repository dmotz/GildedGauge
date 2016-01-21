(ns gilded-gauge.presets
  (:require [gilded-gauge.data :as data]
            [gilded-gauge.utils :refer [format-number]]))


(def $format (comp (partial str \$) format-number))

(defn $zip [xs]
  (zipmap (map $format xs) xs))


(def amounts ($zip [1 5 10 50 100 500 1000 5000]))


(def worths
  {"median U.S. household income"   data/median-us-income
   "median global household income" data/median-global-income})


(def net-worth-min 5)
(def net-worth-max 5e5)
