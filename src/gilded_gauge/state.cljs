(ns gilded-gauge.state
  (:require [gilded-gauge.data :as data]))


(defonce app
  (atom {:current-person     (rand-int (count data/ranked))
         :net-worth          40000
         :amount             100
         :show-person-select false}))
