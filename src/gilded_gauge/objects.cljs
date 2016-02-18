(ns gilded-gauge.objects)

(defn- obj [name price chance]
  {:name name :price price :chance chance})

(def objects
  {:apple      ["apple" 0.5]
   :beer       ["beers" 6]
   ;:boat []
   :burger     ["cheeseburgers" 5]
   :car        ["new cars" 33560]
   :champagne  ["bottles of Dom Perignon" 200]
   :cocktail   ["martinis" 10]
   :coffee     ["cups of coffee" 4]
   ;:crown []
   :diamond    ["Hope Diamonds" 25e7]
   ;:dress []
   ;:euro []
   ;:hat []
   :helicopter ["helicopters" 5e5]
   :horse      ["racehorses" 1e5]
   :house      ["houses" 2e5]
   :iphone     ["iPhones" 700]
   :jeans      ["designer jeans" 100]
   :laptop     ["laptops" 2000]
   :manicure   ["manicures" 15]
   :motorcycle ["motorcycles" 15e3]
   :plane      ["private jets" 3e7]
   ;:pound []
   :purse      ["Louis Vuitton bags" 2000]
   :racecar    ["Lamborghinis" 2e5]
   :sailboat   ["sailboats" 5e4]
   :shoe       ["Christian Louboutin shoes" 700]
   ;:speedboat []
   :stack      ["stacks of 10000 dollars" 1e4]
   :sunglasses ["pairs of designer sunglasses" 200]
   :ticket     ["movie tickets" 10]
   :tiger      ["live tigers" 5e4]
   :yacht      ["superyachts" 275e6]})

(def obj-keys (keys objects))

; fix for small value hunting
(defn create-menagerie [budget]
  (loop [total 0 budget budget items {}] ; track iteration
    (if (>= total budget)
      items
      (let [key   (rand-nth obj-keys)
            item  (key objects)
            price (second item)]
        (if (< price budget)
          (recur
            (+ total price)
            (- budget price)
            (update items key (fnil inc 0)))
          (recur total budget items))))))
