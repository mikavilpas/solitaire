(ns ^:figwheel-always solitaire.logic-test
    #?(:cljs
       (:require [solitaire.logic :as l]
                 [cljs.test :as test])
       :clj
       (:require [solitaire.logic :as l]
                 [clojure.test :as test :refer (is deftest)]))
    #?(:cljs
       (:require-macros [cljs.test :refer (is deftest run-tests)])))

#?(:cljs
   ;; nice test reporting to the js console
   (defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
     (when (not (cljs.test/successful? m))
       (println "Tests FAILED"))))



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
  (is (= {:foundation1 () :foundation2 () :foundation3 () :foundation4 ()
          :foundation5 () :foundation6 () :tableau () :waste-heap () :stock ()}
         (l/remove-card {:foundation1 [(l/card :heart :3)]}
                        "♥3")))
  (is (= {:foundation2 () :foundation1 () :foundation3 () :foundation4 ()
          :foundation5 () :foundation6 () :tableau () :waste-heap () :stock ()}
         (l/remove-card {:foundation2 [(l/card :heart :3)]}
                        "♥3"))))

(deftest add-cards-on-top-of-card-test
  ;; add one card
  (is (= (:foundation1 (l/add-cards-on-top-of-card
                        {:foundation1 (list (l/card :spade :8))}
                        [(l/card :heart :7)]
                        (l/card :spade :8)))

         [{:rank :7, :suite :heart, :id "♥7"}
          {:rank :8, :suite :spade, :id "♠8"}])))

#?(:cljs (run-tests))
