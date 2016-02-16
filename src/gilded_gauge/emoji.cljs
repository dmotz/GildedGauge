(ns gilded-gauge.emoji)

(def M js/Matter)
(def Engine (.-Engine M))
(def World (.-World M))
(def Bodies (.-Bodies M))
(def Composite (.-Composite M))


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


(defn add-wall [world x y w h]
  (.add World world (.rectangle Bodies x y w h #js {:isStatic true}))
  world)


(defn add-body [world w _]
  (js/setTimeout
    #(.add World
      world
      (.circle
        Bodies
        (rand-int w)
        -100
        12
        #js {:render
             #js {:sprite
                  #js {:texture (str "/images/"
                                     (name (rand-nth sprite-ks))
                                     ".png")}}}))
    (rand-int 5000)))


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
