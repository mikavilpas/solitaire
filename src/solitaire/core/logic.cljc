(ns solitaire.core.logic)

(defn card [suite rank & {:keys [facing-up]
                          :or {facing-up true}}]
  (let [id (str (case suite
                  :spade "♠"
                  :heart "♥"
                  :diamond "♦"
                  :club "♣")
                (name rank))]
    {:rank rank, :suite suite, :id id, :facing-up facing-up}))

(def ranks-ascending [:A :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K])

(defn same-suite [a b] (= (:suite a) (:suite b)))

(defn rank-as-symbol
  [card]
  (name (:rank card)))

(defn rank-as-number [card]
  (let [rank-values (zipmap ranks-ascending (range 1 15))]
    (get rank-values (:rank card))))

(def new-deck
  (let [cards
        (for [suite [:spade :club :heart :diamond]
              rank [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]]
          (card suite rank))]
    (shuffle cards)))

(defn turn-face-down [cards]
  (map #(assoc-in % [:facing-up] false) cards))

(defn- turn-rest-down [cards]
  (if (> (count cards) 1)
    (reverse (cons (first cards)
                   (turn-face-down (rest cards))))
    cards))

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
     :foundation1 []
     :foundation2 []
     :foundation3 []
     :foundation4 []
     ;; A 2 3 4 5 6 7 8 9 10 J Q K
     :tableau1 (turn-rest-down tableau1)
     :tableau2 (turn-rest-down tableau2)
     :tableau3 (turn-rest-down tableau3)
     :tableau4 (turn-rest-down tableau4)
     :tableau5 (turn-rest-down tableau5)
     :tableau6 (turn-rest-down tableau6)
     :waste-heap []
     :stock (turn-face-down stock)
     ;; user selects a place, it gets put here.
     ;; then another click will move the selected cards there, if
     ;; possible
     :selected-place nil}))

(defn can-be-put-on-tableau?
  "new-card is the card that will be placed on top, card-under is the
  card under it, or nil if there is no card under"
  [new-card card-under]  
  (if (not card-under)
    ;; a king can be put on the tableau by itself
    (= :K (:rank new-card))
    (let [different-suite (not (same-suite new-card card-under))
          rank-descending (= (rank-as-number new-card)
                             (dec (rank-as-number card-under)))]
      (and different-suite rank-descending))))

(defn can-be-put-on-foundation?
  "like can-be-put-on-tableau? but for foundations"
  [new-card card-under]
  (if (not card-under)
    (= :A (:rank new-card))
    (let [same-suite (same-suite new-card card-under)
          rank-ascending (= (dec (rank-as-number new-card))
                            (rank-as-number card-under))]
      (and same-suite rank-ascending))))

(defn- card-ids-equal [card-id card]
  (= card-id (:id card)))

(def card-places [:tableau1 :tableau2 :tableau3
                  :tableau4 :tableau5 :tableau6
                  :foundation1 :foundation2 :foundation3
                  :foundation4 :waste-heap :stock])

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

(defn remove-cards [game-state source-card-ids]
  (let [same-id (fn [card]
                  (some #(= % (:id card))
                        source-card-ids))]
    (update-cards game-state
                  (fn [cards card-place]
                    (remove same-id cards)))))

(defn add-cards-on-place [game-state cards card-place]
  (update-in game-state
             [card-place]
             #(concat % cards)))

(defn move-cards-on-place
  ([game-state cards card-place]
   (-> game-state
       (remove-cards (map :id cards))
       (add-cards-on-place cards card-place)
       (assoc :selected-place nil))))

(defn sublists
  "Example: called with [1 2 3], will return '((1 2 3) (2 3) (3))"
  [elements]
  (take (count elements)
        (iterate next elements)))

(def foundations (set [:foundation1 :foundation2
                       :foundation3 :foundation4]))

(defn get-moveable-source-cards [game-state
                                 source-card-place]
  ;; fanned card-places only allow moving the very last card in one go
  (if (or (foundations source-card-place)
          (= :waste-heap source-card-place))
    (list (last (get game-state source-card-place)))
    (filter :facing-up (get game-state source-card-place))))

(defn get-moveable-cards [game-state
                          source-card-place
                          target-card-place]
  (let [source-cards (get-moveable-source-cards game-state
                                                source-card-place)
        target-card (last (get game-state target-card-place))]
    (cond
      (#{:tableau1 :tableau2 :tableau3
         :tableau4 :tableau5 :tableau6} target-card-place)
      (some (fn [cards]
              (when (can-be-put-on-tableau? (first cards)
                                            target-card)
                cards))
            (sublists source-cards))

      (#{:foundation1 :foundation2
         :foundation3 :foundation4} target-card-place)
      ;; only one card can be moved in one go
      (let [source-card (last source-cards)]
        (when (can-be-put-on-foundation? source-card target-card)
          [source-card])))))

(defn move-card-place-cards-to [game-state
                                source-card-place
                                target-card-place]
  ;; get the greatest set of cards that can be moved in sequence
  (if-let [cards-to-move (get-moveable-cards game-state
                                             source-card-place
                                             target-card-place)]
    (move-cards-on-place game-state
                         cards-to-move
                         target-card-place)
    game-state))

(defn turn-card [game-state card-to-turn card-place-name]
  (cond (= :stock card-place-name)
        (-> game-state
            (move-cards-on-place [card-to-turn] :waste-heap)
            (update-card
             (:id card-to-turn)
             (fn [card card-place]
               (assoc-in card [:facing-up] true))))

        true
        (let [topmost-card (-> (get game-state card-place-name)
                               last)]

          ;; should turn card only when it's on the top of the pile
          (if (card-ids-equal (:id topmost-card) card-to-turn)
            (update-card
             game-state
             (:id card-to-turn)
             (fn [card card-place]
               (assoc-in card [:facing-up] true)))

            game-state))))

(defn reset-stock
  "Moves the cards from waste-heap to the stock"
  [game-state]
  (-> game-state
      (move-cards-on-place (:waste-heap game-state) :stock)
      (update-in [:stock] (fn [cards]
                            (-> cards
                                turn-face-down
                                reverse)))))

(defn get-hints
  "Returns places where the current card can be moved"
  [game-state source-card-place]
  (let [source-card (last (get game-state source-card-place))
        other-places (filter #(not (= :stock %)) card-places)]
    (reduce (fn [result target-card-place]
              (let [foo (get-moveable-cards game-state
                                            source-card-place
                                            target-card-place)]
                (if-not (empty? foo)
                  (conj result target-card-place)
                  result)))
            []
            other-places)))
