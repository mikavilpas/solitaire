(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]))

;; todo disallow selecting stock somehow
(defn select-or-move! [app-state card-place-name]
  (cond (or (= :stock card-place-name)
            (empty? (get @app-state card-place-name)))
        (swap! app-state assoc-in [:selected-place] nil)

        ;; valid card place?
        ((set l/card-places) card-place-name)
        (swap! app-state assoc-in [:selected-place] card-place-name)))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn reset-stock! [app-state]
  (swap! app-state l/reset-stock))
