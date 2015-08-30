(ns solitaire.logic-test
  #?(:cljs
     (:require [solitaire.logic :as l]
               [cljs.test :as test])
     :clj
     (:require [solitaire.logic :as l]
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
  (is (= {:tableau1 () :tableau2 () :tableau3 () :tableau4 ()
          :tableau5 () :tableau6 () :foundations () :waste-heap () :stock ()}
         (l/remove-card {:tableau1 [(l/card :heart :3)]}
                        "♥3")))
  (is (= {:tableau2 () :tableau1 () :tableau3 () :tableau4 ()
          :tableau5 () :tableau6 () :foundations () :waste-heap () :stock ()}
         (l/remove-card {:tableau2 [(l/card :heart :3)]}
                        "♥3"))))

;;todo

(deftest move-cards-on-place-test
  ;; add one card
  (is (= '({:rank :7, :suite :heart, :id "♥7", :facing-up true}
           {:rank :8, :suite :spade, :id "♠8", :facing-up true})

         (:tableau1 (l/move-cards-on-place
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
         (-> (l/turn-card {:tableau1 [{:rank :3 :suite :heart
                                       :id "♥3" :facing-up false}]}
                          (l/card :heart :3)
                          :tableau1)
             :tableau1 first)))

  ;; turns a card in the stock and moves it to the waste-heap
  (is (= {:stock []
          :tableau1 []
          :tableau2 []
          :tableau3 []
          :tableau4 []
          :tableau5 []
          :tableau6 []
          :foundations []
          :waste-heap [{:rank :3, :suite :heart, :id "♥3", :facing-up true}]}
         (l/turn-card {:stock [{:rank :3, :suite :heart,
                                :id "♥3", :facing-up false}]}
                      (l/card :heart :3)
                      :stock))))
