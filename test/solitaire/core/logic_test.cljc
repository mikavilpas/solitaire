(ns solitaire.core.logic-test
  #?(:cljs
     (:require [solitaire.core.logic :as l]
               [cljs.test :as test])
     :clj
     (:require [solitaire.core.logic :as l]
               [clojure.test :as test :refer (is deftest)]))
  #?(:cljs
     (:require-macros [cljs.test :refer (is deftest)])))

;; tests
(deftest same-suite-test
  (is (l/same-suite (l/card :heart :3)
                    (l/card :heart :4)))
  (is (not (l/same-suite (l/card :heart :3)
                         (l/card :spade :4)))))

;; todos/ideas:
;; weird extra card as the last card of :stock: {:facing-up true}
;; win screen
;; moving card animation
;; touch device support (swipes)
;; auto moves
;; hint

(deftest rank-as-number-test
  (is (= (range 1 14)
         (mapv l/rank-as-number
               (for [i l/ranks-ascending]
                 (l/card :heart i))))))

(deftest can-be-put-on-tableau-test
  ;; same rank -> no
  (is (not (l/can-be-put-on-tableau? (l/card :heart :3)
                                     (l/card :spade :3))))

  ;; same suite -> no
  (is (not (l/can-be-put-on-tableau? (l/card :spade :3)
                                     (l/card :spade :4))))

  ;; different rank but same suite -> no
  (is (not (l/can-be-put-on-tableau? (l/card :spade :3)
                                     (l/card :spade :4))))

  ;; different suite but same rank -> no
  (is (not (l/can-be-put-on-tableau? (l/card :heart :4)
                                     (l/card :spade :4))))

  ;; rank must be descending by exactly 1
  (is (not (l/can-be-put-on-tableau? (l/card :heart :3)
                                     (l/card :club :5))))

  ;; different suite and rank, rank is ascending -> no
  (is (not (l/can-be-put-on-tableau? (l/card :heart :3)
                                     (l/card :spade :2))))
  (is (not (l/can-be-put-on-tableau? (l/card :heart :J)
                                     (l/card :spade :10))))

  ;; different suite and rank, rank is descending -> yes!
  (is (l/can-be-put-on-tableau? (l/card :spade :4)
                                (l/card :heart :5)))
  (is (l/can-be-put-on-tableau? (l/card :spade :10)
                                (l/card :heart :J))))

(deftest can-be-put-on-foundation?-test
  ;; different suite -> no
  (is (not (l/can-be-put-on-foundation? (l/card :heart :3)
                                        (l/card :spade :4))))

  ;; same suite but new card not exactly 1 larger in rank
  (is (not (l/can-be-put-on-foundation? (l/card :heart :6)
                                        (l/card :heart :4))))

  ;; same suite and rank ascending by 1
  (is (l/can-be-put-on-foundation? (l/card :heart :5)
                                   (l/card :heart :4)))

  ;; an ace can be put on nothing
  (is (l/can-be-put-on-foundation? (l/card :heart :A)
                                   nil))

  ;; but any other card cannot
  (is (not (l/can-be-put-on-foundation? (l/card :heart :J)
                                        nil)))

  (is (l/can-be-put-on-foundation? (l/card :club :2)
                                   (l/card :club :A))))

(deftest remove-card-test
  (is (= {:tableau1 (),:foundation2 (),:tableau6 (),:waste-heap (),
          :tableau2 (),:foundation3 (),:tableau5 (),:tableau3 (),
          :foundation1 (),:tableau4 (),:stock (),:foundation4 (),
          :tableau7 ()}

         (l/remove-cards {:tableau1 [(l/card :heart :3)]}
                         ["♥3"]))))

(deftest add-cards-on-place-test
  ;; add one card
  (is (= '({:rank :8, :suite :spade, :id "♠8", :facing-up true}
           {:rank :7, :suite :heart, :id "♥7", :facing-up true})

         (:tableau1 (l/add-cards-on-place
                     {:tableau1 [(l/card :spade :8)]}
                     [(l/card :heart :7)]
                     :tableau1)))))

(deftest new-game-state-test
  ;; stock cards should face down
  (is (every? (comp not :facing-up)
              (:stock (l/new-game-state)))))

(deftest turn-card-test
  ;; turns card facing up
  (is (= {:rank :3, :suite :heart, :id "♥3", :facing-up true}
         (-> (l/turn-card {:tableau1 '({:rank :3 :suite :heart
                                        :id "♥3" :facing-up false})}
                          (l/card :heart :3)
                          :tableau1)
             :tableau1 first)))

  ;; should not turn card if some other card is on top of it
  (is (= [{:rank :3, :suite :heart, :id "♥3", :facing-up false}
          {:rank :K, :suite :spade, :id "♠K", :facing-up false}]
         (-> (l/turn-card {:tableau1 [(l/card :heart :3 :facing-up false)
                                      (l/card :spade :K :facing-up false)]}
                          (l/card :heart :3)
                          :tableau1)
             :tableau1)))

  ;; turns a card in the stock and moves it to the waste-heap
  (is (= {:tableau1 (),
          :foundation2 (),
          :tableau6 (),
          :tableau7 (),
          :waste-heap [(l/card :heart :3)],
          :tableau2 (),
          :selected-place nil,
          :foundation3 (),
          :tableau5 (),
          :tableau3 (),
          :foundation1 (),
          :tableau4 (),
          :stock [(l/card :heart :4 :facing-up false)],
          :foundation4 ()}

         (l/turn-card {:stock [(l/card :heart :4 :facing-up false)
                               (l/card :heart :3 :facing-up false)]}
                      (l/card :heart :3 :facing-up false)
                      :stock))))

(deftest reset-stock-test
  (is (= {:tableau1 (),
          :foundation2 (),
          :tableau6 (),
          :tableau7 (),
          :waste-heap (),
          :tableau2 (),
          :selected-place nil,
          :foundation3 (),
          :tableau5 (),
          :tableau3 (),
          :foundation1 (),
          :tableau4 (),
          :stock
          [{:rank :5, :suite :heart, :id "♥5", :facing-up false}
           {:rank :4, :suite :heart, :id "♥4", :facing-up false}
           {:rank :3, :suite :heart, :id "♥3", :facing-up false}],
          :foundation4 ()}

         (l/reset-stock {:waste-heap [(l/card :heart :3)
                                      (l/card :heart :4)
                                      (l/card :heart :5)]}))))

(deftest sublists-test
  (is (= [[1 2 3] [2 3] [3]]
         (l/sublists [1 2 3]))))

(deftest get-moveable-cards-test
  ;; returns a single card
  (is (= [(l/card :heart :3)]
         (l/get-moveable-cards {:tableau1 [(l/card :heart :3)]
                                :tableau2 [(l/card :spade :4)]}
                               :tableau1
                               :tableau2)))

  ;; ignores cards that are not :facing-up
  (is (= nil
         (l/get-moveable-cards {:tableau1 [(l/card :spade :4 :facing-up false)
                                           (l/card :heart :3)]
                                :tableau2 [(l/card :club :5)]}
                               :tableau1
                               :tableau2)))

  ;; returns many cards even when the topmost source-card doesn't
  ;; go on top of target-card
  (is (= [(l/card :spade :4)
          (l/card :heart :3)]
         (l/get-moveable-cards {:tableau1 [(l/card :heart :8)
                                           (l/card :spade :4)
                                           (l/card :heart :3)]
                                :tableau2 [(l/card :heart :5)]}
                               :tableau1
                               :tableau2)))

  ;; it's only possible to move the last card from fanned places
  (is (empty?
       (l/get-moveable-cards {:waste-heap [(l/card :spade :4)
                                           (l/card :heart :3)]
                              :tableau1 [(l/card :heart :5)]}
                             :waste-heap
                             :tableau1))))

(deftest move-card-place-cards-to-test
  ;; only the topmost card can be moved to foundations at once
  (is (= [(l/card :club :A)]
         (-> (l/move-card-place-cards-to
              {:waste-heap [(l/card :diamond :3)
                            (l/card :spade :7)
                            (l/card :spade :4)
                            (l/card :club :A)]
               :foundation1 []}
              :waste-heap
              :foundation1)
             :foundation1)))

  ;; swaps selection if no cards can be moved
  (is (= :foundation2
         (-> (l/move-card-place-cards-to
              {:foundation1 [(l/card :diamond :3)]
               :foundation2 [(l/card :heart :3)]}
              :foundation1
              :foundation2)
             :selected-place))))

(deftest get-hints-test
  (is (= (set [:tableau1 :foundation1])
         (l/get-hints {:waste-heap [(l/card :heart :3)]
                       :foundation1 [(l/card :heart :2)]
                       :tableau1 [(l/card :spade :4)]}
                      :waste-heap))))

(deftest auto-move-test
  (let [result (l/auto-move {:tableau1 [(l/card :heart :A)]}
                            :tableau1)]
    (is (= []
           (:tableau1 result)))
    (is (= [(l/card :heart :A)]
           (:foundation2 result)))))
