(ns gilded-gauge.data)


;; https://en.wikipedia.org/wiki/The_World%27s_Billionaires_2015

(def ranked
  (partition
   2
   ["Bill Gates"          79.2
    "Carlos Slim"         77.1
    "Warren Buffett"      72.7
    "Amancio Ortega"      64.5
    "Larry Ellison"       54.3
    "Charles Koch"        42.9
    "David Koch"          42.9
    "Christy Walton"      41.7
    "Jim Walton"          40.6
    "Liliane Bettencourt" 40.1]))

(def img-url-root "https://upload.wikimedia.org/wikipedia/commons/thumb/")

(def images
  (into
    {}
    (map
      (fn [[k v]] [k (str img-url-root v)])
      {"Bill Gates"     "1/19/Bill_Gates_June_2015.jpg/440px-Bill_Gates_June_2015.jpg"
       "Carlos Slim"    "d/df/Carlos_Slim_Hel%C3%BA.jpg/440px-Carlos_Slim_Hel%C3%BA.jpg"
       "Warren Buffett" "5/51/Warren_Buffett_KU_Visit.jpg/440px-Warren_Buffett_KU_Visit.jpg"
       "Larry Ellison"  "3/3c/Larry_Elllison_on_stage.jpg/440px-Larry_Elllison_on_stage.jpg"
       "David Koch"     "3/36/David_Koch_by_Gage_Skidmore.jpg/440px-David_Koch_by_Gage_Skidmore.jpg"
       "Jim Walton"     "b/bb/Jim_Walton_attends_shareholders_meeting.jpg/440px-Jim_Walton_attends_shareholders_meeting.jpg"})))


(def median-us-income 53657)
(def median-global-income 9733)

(def stats
  {"years of median U.S. household income"   median-us-income
   "years of median global household income" median-global-income})
