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

(deftest rank-as-number-test
  (is (= (range 1 15)
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

  ;; different suite and rank, rank is descending -> no
  (is (not (l/can-be-put-on-tableau? (l/card :heart :5)
                                     (l/card :spade :4))))
  (is (not (l/can-be-put-on-tableau? (l/card :heart :J)
                                     (l/card :spade :10))))

  ;; different suite and rank, rank is ascending -> yes!
  (is (l/can-be-put-on-tableau? (l/card :spade :10)
                                (l/card :heart :J))))

(deftest remove-card-test
  (is (= {:tableau1 (),:foundation2 (),:tableau6 (),:waste-heap (),
          :tableau2 (),:foundation3 (),:tableau5 (),:tableau3 (),
          :foundation1 (),:tableau4 (),:stock (),:foundation4 ()}

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
  (is (= [{:rank :3, :suite :heart, :id "♥3", :facing-up true}
          {:rank :K, :suite :spade, :id "♠K", :facing-up false}]
         (-> (l/turn-card {:tableau1 [(l/card :heart :3)
                                      (l/card :spade :K :facing-up false)]}
                          (l/card :spade :K)
                          :tableau1)
             :tableau1)))

  ;; turns a card in the stock and moves it to the waste-heap
  (is (= {:tableau1 (),
          :foundation2 (),
          :tableau6 (),
          :waste-heap [{:rank :3, :suite :heart, :id "♥3", :facing-up true}],
          :tableau2 (),
          :selected-place nil,
          :foundation3 (),
          :tableau5 (),
          :tableau3 (),
          :foundation1 (),
          :tableau4 (),
          :stock (),
          :foundation4 ()}

         (l/turn-card {:stock [{:rank :3, :suite :heart,
                                :id "♥3", :facing-up false}]}
                      (l/card :heart :3 :facing-up false)
                      :stock))))

(deftest reset-stock-test
  (is (= {:tableau1 (),
          :foundation2 (),
          :tableau6 (),
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
