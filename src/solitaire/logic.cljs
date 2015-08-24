(ns solitaire.logic)

(defn card [suite rank]
  {:rank rank, :suite suite})

(def ranks-ascending [:A :1 :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K])

(defn same-suite [a b] (= (:suite a) (:suite b)))

(defn rank-as-number [card]
  (let [rank-values (zipmap ranks-ascending (range 1 15))]
    ((:rank card) rank-values)))

(defn rank-as-symbol
  [card]
  (name (:rank card)))

(def deck
  (let [cards
        (for [suite [:spade :club :heart :diamond]
              rank [:1 :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]]
          (card suite rank))]
    (shuffle cards)))

(defn new-game-state []
  ;; I don't know solitaire terms so I just copy these from wikipedia
  (let [take-cards (fn [n remaining-deck]
                     (split-at (rand-int n)
                               remaining-deck))
        stock deck
        ;; keep taking cards from the stock. the stock is "mutated" each time
        [foundation1 stock] (take-cards 6 stock)
        [foundation2 stock] (take-cards 6 stock)
        [foundation3 stock] (take-cards 6 stock)
        [foundation4 stock] (take-cards 6 stock)
        [foundation5 stock] (take-cards 6 stock)
        [foundation6 stock] (take-cards 6 stock)]
    { ;; K Q J 10 9 8 7 6 5 4 3 2 A
     :foundation1 foundation1
     :foundation2 foundation2
     :foundation3 foundation3
     :foundation4 foundation4
     :foundation5 foundation5
     :foundation6 foundation6
     ;; A 2 3 4 5 6 7 8 9 10 J Q K
     :tableau []
     :waste-heap {}
     :stock stock
     ;; user selects a card, it gets put here.
     ;; then another click will move the selected card somewhere
     :selected-card nil}))

(defn can-be-put-on-tableau?
  "a is the new card, b is the card under it, or nil if there is no
  card under"
  [a b]
  (let [different-suite (not (same-suite a b))
        rank-descending (< (rank-as-number a)
                           (rank-as-number b))]
    (and different-suite rank-descending)))

(defn card-symbol [card]
  (when card
    (case (:suite card)
      :spade "♠"
      :heart "♥"
      :diamond "♦"
      :club "♣")))
