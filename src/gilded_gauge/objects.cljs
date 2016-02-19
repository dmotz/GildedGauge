(ns gilded-gauge.objects)


(def objects
  {:apple      ["apples" 0.5]
   :banana     ["bananas" 0.5]
   :beer       ["beers" 6]
   ;:boat []
   :burger     ["cheeseburgers" 5]
   :burrito    ["burritos" 7]
   :car        ["new cars" 33560]
   :champagne  ["bottles of Dom Perignon" 200]
   :cheese     ["pounds of Beaufort d'été cheese" 35]
   :cocktail   ["martinis" 10]
   :coffee     ["cups of coffee" 4]
   ;:crown []
   :diamond    ["Hope Diamonds" 25e7]
   :donut      ["donuts" 0.5]
   ;:dress []
   ;:drink []
   ;:euro []
   ;:hat []
   :helicopter ["helicopters" 5e5]
   :horse      ["racehorses" 1e5]
   :hotdog     ["hotdogs" 3]
   :house      ["houses" 2e5]
   :iphone     ["iPhones" 700]
   :jeans      ["pairs of designer jeans" 100]
   :laptop     ["laptops" 2000]
   :manicure   ["manicures" 15]
   :motorcycle ["motorcycles" 15e3]
   :orange     ["oranges" 0.5]
   :peach      ["peaches" 0.5]
   :pear       ["pears" 0.5]
   :pizza      ["slices of pizza" 3]
   :plane      ["private jets" 3e7]
   ;:pound []
   :purse      ["Louis Vuitton bags" 2000]
   :racecar    ["Lamborghinis" 2e5]
   :sailboat   ["sailboats" 5e4]
   :shoe       ["Christian Louboutin shoes" 700]
   ;:speedboat []
   :stack      ["stacks of 10000 dollars" 1e4]
   :sunglasses ["pairs of designer sunglasses" 200]
   :taco       ["tacos" 2]
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
