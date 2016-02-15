(ns gilded-gauge.emoji
  (:require [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))


(def P js/Physics)



(def Text (.-Text js/PIXI))
(def Sprite (.-Sprite js/PIXI))
(def Texture (.-Texture js/PIXI))
(def Rectangle (.-Rectangle js/PIXI))

(def WebGLRenderer (.-WebGLRenderer js/PIXI))

(def pixi-renderer (WebGLRenderer. 10 10))


(def objects
  {:diamond    [5000 0.7]
   :stack      [100 0.9]
   :iphone     [600 0.5]
   :house      []
   :car        []
   :helicopter []
   :purse      []
   :crown      []
   :beer []
   :boat []
   :burger []
   :champagne []
   :cocktail []
   :coffee []
   :dress []
   :euro []
   :hat []
   :horse []
   :jeans []
   :laptop []
   :manicure []
   :motorcycle []
   :plane []
   :pound []
   :racecar []
   :sailboat []
   :shoe []
   :speedboat []
   :sunglasses []
   :ticket []
   :tiger []
   :yacht []
   :yen []})




(def sprite-ks (keys objects))


(def sprites
  (into
    {}
    (map
      (fn [k]
        [k (Sprite. (.fromImage Texture (str "/images/" (name k) ".png")))])
      sprite-ks)))



(defn- behavior
  ([type]
   (.behavior P type))
  ([type config]
   (.behavior P type config)))


(defn- body [config]
  (.body P "circle" config))


(defn- add [world obj]
  (.add world obj))


(defn init [w h]
  (let [renderer
        (.renderer P "pixi" #js {:el "canvas-left" :width w :height h :resolution 1 :meta true})

        sprites
        (into
          {}
          (map
            (fn [k]
              [k (.createDisplay renderer "sprite" #js {:texture (str "/images/" (name k) ".png")})])
            sprite-ks))

        make-body
        (fn [config]
          (body #js {:x (rand w)
                     :y (- (rand h))
                     :radius 15
                     :mass 2
                     :restitution 0.5
                     :view
                     (.createDisplay renderer "sprite" #js {:texture (str "/images/" (name (rand-nth sprite-ks)) ".png")
                                                            :anchor #js {:x 0.5 :y 0.5}})}))]

    (P
      #js {:sleepTimeLimit 500}
      (fn [world]
        (->
          (.. P -util -ticker)
          (.on #(.step world %))
          .start)

        (->
          world
          (add
            #js
            [(behavior
               "edge-collision-detection"
               #js {:aabb (.aabb P 0 -50 w (- h 30)) :restitution 0.5 :cof 0.8})
             (behavior "constant-acceleration" #js {:x 0 :y 0.0004})
             (behavior "body-impulse-response")
             (behavior "body-collision-detection")
             (behavior "sweep-prune")])

          (add renderer)
          (.on "step" #(.render world)))

        #_
        (go-loop [bodies (map make-body (range 100))]
          (<! (timeout (rand-int 200)))
          (when (seq bodies)
            (add world (first bodies))
            (recur (rest bodies))))

        (doseq [body (map make-body (range 100))]
          (js/setTimeout #(add world body) (rand-int 3000)))))))
