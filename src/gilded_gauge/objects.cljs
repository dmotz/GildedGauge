(ns gilded-gauge.objects)


(def objects
  {:apple      ["apple?" 0.5]
   :banana     ["banana?" 0.5]
   :beer       ["beer?" 6]
   ;:boat []
   :burger     ["cheeseburger?" 5]
   :burrito    ["burrito?" 7]
   :car        ["new car?" 33560]
   :champagne  ["bottle? of Dom Perignon" 200]
   :cheese     ["pound? of Beaufort d’été cheese" 35]
   :cocktail   ["martini?" 10]
   :coffee     ["cup? of coffee" 4]
   ;:crown []
   :diamond    ["Hope Diamond?" 25e7]
   :donut      ["donut?" 0.5]
   ;:dress []
   ;:drink []
   ;:hat []
   :helicopter ["helicopter?" 5e5]
   :horse      ["racehorse?" 1e5]
   :hotdog     ["hotdog?" 3]
   :house      ["house?" 2e5]
   :iphone     ["iPhone?" 700]
   :jeans      ["pair? of designer jeans" 100]
   :laptop     ["laptop?" 2000]
   :manicure   ["manicure?" 15]
   :motorcycle ["motorcycle?" 15e3]
   :orange     ["orange?" 0.5]
   :pear       ["pear?" 0.5]
   :pizza      ["slice? of pizza" 3]
   :plane      ["private jet?" 3e7]
   :purse      ["Louis Vuitton bag?" 2000]
   :racecar    ["Lamborghini?" 2e5]
   :sailboat   ["sailboat?" 5e4]
   :shoe       ["pair? of Christian Louboutin shoes" 700]
   :speedboat  ["luxury speedboat?" 3e5]
   :stack      ["stack? of 10,000 dollars" 1e4]
   :sunglasses ["pair? of Gucci sunglasses" 250]
   :taco       ["taco?" 2]
   :ticket     ["movie ticket?" 10]
   :tiger      ["live tiger?" 5e4]
   :watch      ["Rolex Oyster Perpetual?" 26500]
   :yacht      ["superyacht?" 275e6]})

(def obj-keys (keys objects))


(defn create-menagerie [budget]
  (loop [total 0 budget-left budget items {} it 0]
    (if (>= total budget)
      ;[items it]
      items
      (let [key   (rand-nth obj-keys)
            item  (key objects)
            price (second item)]
        (if (<= price budget-left)
          (recur
            (+ total price)
            (- budget-left price)
            (update items key (fnil inc 0))
            (inc it))
          (recur total budget-left items (inc it)))))))


(defn test-m [n b]
  (dotimes [_ n]
    (let [[m it] (create-menagerie b)
          t (reduce (fn [a [k v]] (+ a (* (second (k objects)) v))) 0 m)]
      ;(prn it)
      ;(prn t)
      (prn (reduce + (vals m)))
      ;(prn (- t b))
      (prn "--------------"))))

;(test-m 10 500000000)
;(dotimes [_ 10]
;  (let [m (create-menagerie 10032452356)]
;    (prn m)))
    ;(prn (reduce + (vals m)))
    ;(prn (mapcat (fn [[k v]] (repeat v k)) m))))
