(ns gilded-gauge.data
  (:require [gilded-gauge.rankings :as rankings]))

(def img-url-root "https://upload.wikimedia.org/wikipedia/")

(def ranked
  (mapv
    (fn [[name worth img]]
      {:name name :worth worth :img (when img (str img-url-root img))})
    rankings/rankings))

(def median-us-income 53657)
(def median-global-income 9733)

(def stats
  {"years of median U.S. household income"   median-us-income
   "years of median global household income" median-global-income})
