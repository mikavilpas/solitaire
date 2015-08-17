(ns solitaire.logic)

(defn card [suite rank]
  {:rank rank, :suite suite})

(def ranks-ascending [:A :1 :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K])

(defn rank-as-number [card]
  (let [rank-values (zipmap ranks-ascending (range 1 15))]
    ((:rank card) rank-values)))

(def deck
  (let [cards
        (for [suite [:spade :club :heart :diamond]
              rank [:1 :2 :3 :4 :5 :6 :7 :8 :9 :10]]
          (card rank suite))]
    (shuffle cards)))

(defn same-suite [a b] (= (:suite a) (:suite b)))

(defn can-be-put-on-tableau
  "a is the new card, b is the card under it, or nil if there is no
  card under"
  [a b]
  (let [different-suite (not (same-suite a b))
        rank-descending (< (rank-as-number a)
                           (rank-as-number b))]
    (and different-suite rank-descending)))
