(ns gilded-gauge.objects)

(def objects
  {:apple      ["apple?" 0.5]
   :banana     ["banana?" 0.5]
   :beer       ["beer?" 6]
   :bicycle    ["bicycle?" 300]
   :burger     ["cheeseburger?" 5]
   :burrito    ["burrito?" 7]
   :car        ["new car?" 33560]
   :champagne  ["bottle? of Dom Perignon" 200]
   :cheese     ["pound? of Beaufort d’été cheese" 35]
   :cocktail   ["martini?" 10]
   :coffee     ["cup? of coffee" 4]
   :diamond    ["Hope Diamond?" 25e7]
   :donut      ["donut?" 0.5]
   :haircut    ["salon haircut?" 70]
   :handbag    ["Louis Vuitton bag?" 2000]
   :helicopter ["helicopter?" 5e5]
   :horse      ["racehorse?" 1e5]
   :hotdog     ["hotdog?" 3]
   :house      ["house?" 2e5]
   :iphone     ["iPhone?" 700]
   :jeans      ["pair? of designer jeans" 100]
   :key        ["annual key? to Gramercy Park" 350]
   :laptop     ["laptop?" 2000]
   :lipstick   ["tube? of lipstick" 25]
   :manicure   ["manicure?" 15]
   :motorcycle ["motorcycle?" 15e3]
   :orange     ["orange?" 0.5]
   :pear       ["pear?" 0.5]
   :pizza      ["slice? of pizza" 3]
   :plane      ["private jet?" 3e7]
   :racecar    ["Lamborghini?" 2e5]
   :sailboat   ["sailboat?" 5e4]
   :shoe       ["pair? of Christian Louboutin shoes" 700]
   :skis       ["pair? of skis" 300]
   :speedboat  ["luxury speedboat?" 3e5]
   :stack      ["stack? of 10,000 dollars" 1e4]
   :sunglasses ["pair? of Gucci sunglasses" 250]
   :taco       ["taco?" 2]
   :ticket     ["movie ticket?" 10]
   :tiger      ["live tiger?" 5e4]
   :violin     ["rare Stradivarius violin?" 1e6]
   :watch      ["Rolex Oyster Perpetual?" 26500]
   :yacht      ["superyacht?" 275e6]})

(def obj-keys (keys objects))

(def threshold 0.0033)

(defn create-menagerie [budget]
  (loop [total 0 budget-left budget items {}]
    (if (>= total budget)
      (into
        (sorted-map-by
          (fn [k1 k2]
            (compare
              [(* (k2 items) (second (k2 objects))) k2]
              [(* (k1 items) (second (k1 objects))) k1])))
        items)

      (let [key   (rand-nth obj-keys)
            item  (key objects)
            price (second item)]
        (if (and (>= (/ price budget-left) threshold) (<= price budget-left))
          (recur
            (+ total price)
            (- budget-left price)
            (update items key (fnil inc 0)))
          (recur total budget-left items))))))


(doseq [k obj-keys]
  (set! (.-src (js/Image.)) (str "/images/" (name k) ".png")))
