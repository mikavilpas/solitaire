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
     :waste-heap {}
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
(defn update-cards [game-state update-function]
  (reduce (fn [result card-place]
            (update-in result [card-place]
                       (fn [cards] (update-function cards))))
          game-state
          card-places))

(defn update-card [game-state update-function]
  (update-cards game-state
                (fn [cards] (map update-function cards))))

;; todo these need tests
(defn remove-card [game-state source-card-id]
  (let [same-id (partial card-ids-equal source-card-id)]
    (update-cards game-state
                  (fn [cards]
                    (remove same-id cards)))))

;; todo shorten
(defn add-cards-on-top-of-card
  "Adds the card with source-cards on top of the card with
  destination-card-id."
  [game-state source-cards destination-card-id]
  (update-cards
   game-state
   (fn [cards]
     (let [[before target-and-rest]
           (split-with #(not (card-ids-equal destination-card-id
                                             (:id %)))
                       cards)]
       (if (not-empty before)
         (flatten [source-cards before target-and-rest])
         cards)))))

(defn turn-card [game-state card-id]
  (update-card game-state
               (fn [card]
                 (if (= (:id card) card-id)
                   (update-in card [:facing-up] not)
                   card))))

(defn move-card [destination-card-id game-state]
  (let [source-card-id (:selected-card-id game-state)]
    (-> game-state
        (remove-card source-card-id)
        ;; (add-card source-card-id destination-card-id)
        )

    ;; nothing to do
    game-state))
