(ns gilded-gauge.presets
  (:require [gilded-gauge.data :as data]))

(def amounts (->> [1 10 50 100 500 1000 5000]
                  repeat
                  (take 2)
                  (apply zipmap)))


(def worths
  {"median U.S. household income"   data/median-us-income
   "median global household income" data/median-global-income})


(def net-worth-min 5)
(def net-worth-max 5e5)
