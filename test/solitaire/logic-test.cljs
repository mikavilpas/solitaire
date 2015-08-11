(ns ^:figwheel-always solitaire.logic-test
    (:require [solitaire.logic :as l]
              [cljs.test :as test])
    (:require-macros [cljs.test :refer (is deftest run-tests)]))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (if (cljs.test/successful? m)
    (println "Tests passed.")
    (println "Tests FAILED")))

(deftest same-suite-test
  (is (l/same-suite (l/card :heart :3)
                    (l/card :heart :4))))

(run-tests)
