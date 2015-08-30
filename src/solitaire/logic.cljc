(ns solitaire.logic)

(defn card [suite rank]
  (let [id (str (case suite
                  :spade "♠"
                  :heart "♥"
                  :diamond "♦"
                  :club "♣")
                (name rank))]
    {:rank rank, :suite suite, :id id, :facing-up true}))

(def ranks-ascending [:A :1 :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K])

(defn same-suite [a b] (= (:suite a) (:suite b)))

(defn rank-as-symbol
  [card]
  (name (:rank card)))

(defn rank-as-number [card]
  (let [rank-values (zipmap ranks-ascending (range 1 15))]
    ((:rank card) rank-values)))

(def new-deck
  (let [cards
        (for [suite [:spade :club :heart :diamond]
              rank [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]]
          (card suite rank))]
    (shuffle cards)))

(defn turn-face-down [cards]
  (map #(assoc-in % [:facing-up] false) cards))

(defn new-game-state []
  (let [take-cards (fn [n remaining-deck]
                     (split-at (rand-int n)
                               remaining-deck))
        stock new-deck
        ;; keep taking cards from the stock. the stock is "mutated" each time
        [tableau1 stock] (take-cards 6 stock)
        [tableau2 stock] (take-cards 6 stock)
        [tableau3 stock] (take-cards 6 stock)
        [tableau4 stock] (take-cards 6 stock)
        [tableau5 stock] (take-cards 6 stock)
        [tableau6 stock] (take-cards 6 stock)]
    { ;; K Q J 10 9 8 7 6 5 4 3 2 A
     :foundations []
     ;; A 2 3 4 5 6 7 8 9 10 J Q K
     :tableau1 tableau1
     :tableau2 tableau2
     :tableau3 tableau3
     :tableau4 tableau4
     :tableau5 tableau5
     :tableau6 tableau6
     :waste-heap []
     :stock (turn-face-down stock)
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

(defn- card-ids-equal [card-id card]
  (= card-id (:id card)))

(def card-places [:tableau1 :tableau2 :tableau3
                  :tableau4 :tableau5 :tableau6
                  :foundations :waste-heap :stock])

;; utility function
(defn update-cards
  "update-function receives two arguments: all the cards in a specific
  pile, and the name of the pile, as in card-places"
  [game-state update-function]
  (reduce (fn [result card-place]
            (update-in result [card-place]
                       (fn [cards] (update-function cards card-place))))
          game-state
          card-places))

(defn update-card
  "like update-cards, except update-function gets only one card at a
  time, as well as the name of the pile the cards are from, as
  specified in card-places. Returns the place of the card and the
  modified card."
  [game-state card-id update-function]
  (update-cards game-state
                (fn [cards card-place]
                  (map (fn [card]
                         (if (= card-id (:id card))
                           (update-function card card-place)
                           card))
                       cards))))

(defn remove-card [game-state source-card-id]
  (let [same-id (partial card-ids-equal source-card-id)]
    (update-cards game-state
                  (fn [cards card-place]
                    (remove same-id cards)))))

(defn move-cards-on-place [game-state cards card-place]
  (update-in game-state
             [card-place]
             #(concat cards %)))

(defn turn-card [game-state card-to-turn card-place-name]
  (let [game-state (update-card
                    game-state
                    (:id card-to-turn)
                    (fn [card card-place]
                      (assoc-in card [:facing-up] true)))]

    ;; if card is on stock, move to waste
    (if (= :stock card-place-name)
      (-> game-state
          (remove-card (:id card-to-turn))
          (move-cards-on-place [card-to-turn] :waste-heap))
      game-state)))
