(ns gilded-gauge.emoji
  (:require [gilded-gauge.objects :refer [objects create-menagerie]]
            [gilded-gauge.utils :refer [set-timeout!]]))

(def body-radius 14)
(def min-width 700)
(def min-height 700)

(def M js/Matter)
(def Engine (.-Engine M))
(def World (.-World M))
(def Body (.-Body M))
(def Bodies (.-Bodies M))
(def Composite (.-Composite M))
(def Vector (.-Vector M))
#_
(def Texture (.-Texture js/PIXI))

#_
(def textures
  (into
    {}
    (doall
      (map
        (fn [[k]]
          [k (.fromImage Texture (str "/images/" (name k) ".png"))])
        objects))))


(defn add-wall [world x y w h]
  (.add World
    world (.rectangle Bodies x y w h #js {:isStatic true
                                          :render   #js {:visible false}}))
  world)


(defn add-body [world max-x time sprite]
  (set-timeout!
    (rand-int time)
    #(.add World
      world
      (.circle
        Bodies
        (rand-int max-x)
        -100
        body-radius
        #js {:restitution 0.5
             :render
             #js {:sprite
                  #js {:yOffset 0
                       :texture (str "/images/" (name sprite) ".png")}}}))))


(defn init [el w h]
  (let [engine (.create
                 Engine
                 #js {:render
                      #js {:element     el
                           :options     #js {:width w :height h}
                           :controller  (.-RenderPixi M)
                           :pixiOptions #js {:transparent true}}})]
    (->
      (.-world engine)
      (add-wall (/ w 2) (+ h 20) 10000 100)
      (add-wall -50 (- (/ h 2) 50) 100 (+ h 100))
      (add-wall (+ w 50) (- (/ h 2) 50) 100 (+ h 100)))

    (.run Engine engine)
    engine))

(def last-size (atom [(/ js/innerWidth 2) js/innerHeight]))

(defn resize! [left right]
  (let [w (/ (Math/max js/innerWidth min-width) 2)
        h (Math/max js/innerHeight min-height)
        [last-w last-h] @last-size]

    (doseq [engine [left right]]
      (let [bodies  (.. engine -world -bodies)]
        (.translate Body (aget bodies 0) (.create Vector 0 (- h last-h)))
        (.translate Body (aget bodies 2) (.create Vector (- w last-w) 0))
        (.resize (.. engine -render -renderer) w h)))

    (reset! last-size [w h])))


(defn run [engine menagerie]
  (let [world (.-world engine)
        floor (aget (.-bodies world) 0)
        time  (-> (count menagerie) Math/log (* 1000) Math/round)]
    (.translate Body floor (.create Vector 0 500))
    (set-timeout!
      1500
      #(do
        (.clear Composite world true)
        (.translate Body floor (.create Vector 0 -500))
        (dorun
          (map
            (partial add-body world (/ js/innerWidth 2) time)
            (mapcat (fn [[k v]] (repeat v k)) menagerie)))))))
