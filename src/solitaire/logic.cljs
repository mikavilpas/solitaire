(ns solitaire.logic)

(defn card [rank suite]
  {:rank rank, :suite suite})

(def deck
  (for [suite [:spade :club :heart :diamond]
        rank [:1 :2 :3 :4 :5 :6 :7 :8 :9 :10]]
    (card rank suite)))
