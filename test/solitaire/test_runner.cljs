(ns ^:figwheel-always
  solitaire.test-runner
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.test]
            [solitaire.logic-test]
            [figwheel.client :as fw])
  (:require-macros [cljs.test :refer (run-tests)]))

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

(enable-console-print!)
(fw/start)

(defonce test-result (atom {:success? false}))

(defn test-report []
  [:div.container
   [:div.col-xs-12
    [:p {:class (str "test-result " (if (:success? @test-result)
                                      "test-success"
                                      "test-failed"))}
     (str "Test results: " (:success? @test-result))]]])

(defn- show-favicon [success?]
  (when-let [link-element (.getElementById js/document "my-favicon")]
    (if success?
      (set! (.-href link-element) "img/plus-1.png")
      (set! (.-href link-element) "img/test-failure-favicon.png"))))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (let [success? (cljs.test/successful? m)]
    (swap! test-result assoc-in [:success?] success?)
    (show-favicon success?)))


(reagent/render-component [test-report]
                          (js/document.getElementById "test"))

(run-tests 'solitaire.logic-test)
