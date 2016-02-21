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
   ;:euro []
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
   ;:peach      ["peaches" 0.5]
   :pear       ["pear?" 0.5]
   :pizza      ["slice? of pizza" 3]
   :plane      ["private jet?" 3e7]
   ;:pound []
   :purse      ["Louis Vuitton bag?" 2000]
   :racecar    ["Lamborghini?" 2e5]
   :sailboat   ["sailboat?" 5e4]
   :shoe       ["pair? of Christian Louboutin shoes" 700]
   ;:speedboat []
   :stack      ["stack? of 10,000 dollars" 1e4]
   :sunglasses ["pair? of designer sunglasses" 200]
   :taco       ["taco?" 2]
   :ticket     ["movie ticket?" 10]
   :tiger      ["live tiger?" 5e4]
   :yacht      ["superyacht?" 275e6]})

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
