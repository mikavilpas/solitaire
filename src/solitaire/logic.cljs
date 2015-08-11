(ns solitaire.logic)

(defn card [suite rank]
  {:rank rank, :suite suite})

(def deck
  (for [suite [:spade :club :heart :diamond]
        rank [:1 :2 :3 :4 :5 :6 :7 :8 :9 :10]]
    (card rank suite)))

(defn same-suite [a b]
  (= (:suite a) (:suite b)))

