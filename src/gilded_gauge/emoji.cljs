(ns gilded-gauge.emoji
  (:require [gilded-gauge.objects :refer [objects create-menagerie]]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(def M js/Matter)
(def Engine (.-Engine M))
(def World (.-World M))
(def Body (.-Body M))
(def Bodies (.-Bodies M))
(def Composite (.-Composite M))
(def Vector (.-Vector M))


(defn add-wall [world x y w h]
  (.add World
    world (.rectangle Bodies x y w h #js {:isStatic true
                                          :render   #js {:visible false}}))
  world)


(defn add-body [world max-x time sprite]
  (js/setTimeout
    #(.add World
      world
      (.circle
        Bodies
        (rand-int max-x)
        -100
        12
        #js {:render
             #js {:sprite
                  #js {:yOffset 0.5
                       :texture (str "/images/" (name sprite) ".png")}}}))
    (rand-int time)))


(defn init [el w h]
  (let [engine (.create
                 Engine
                 #js {:render
                      #js {:element    el
                           :options    #js {:width w :height h}
                           :controller (.-RenderPixi M)}})]
    (->
      (.-world engine)
      (add-wall (/ w 2) (- h 30) w 1)
      (add-wall -1 (/ h 2) 1 h)
      (add-wall (inc w) (/ h 2) 1 h))

    (.run Engine engine)
    engine))


(defn resize [engine]
  (let [w      (/ js/innerWidth 2)
        h      js/innerHeight
        bounds (.. engine -world -bounds -max)
        canvas (.. engine -render -canvas)]

    (set! (.-x bounds) w)
    (set! (.-y bounds) h)
    (set! (.-width canvas) w)
    (set! (.-height canvas) h)))
    ; move right wall
    ;(.translate Body (aget (.-bodies world) 2) (.create Vector 0 0))))


(defn run [engine menagerie]
  (let [world (.-world engine)
        floor (aget (.-bodies world) 0)
        time  (-> (count menagerie) Math/log (* 1000) Math/round)]
    (go
      (.translate Body floor (.create Vector 0 500))
      (<! (timeout 1500))
      (.clear Composite world true)
      (.translate Body floor (.create Vector 0 -500))
      (dorun
        (map
          (partial add-body world (/ js/innerWidth 2) time)
          (mapcat (fn [[k v]] (repeat v k)) menagerie))))))
