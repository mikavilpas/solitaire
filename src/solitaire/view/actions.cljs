(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]))

(defn select-or-move! [app-state target-card-place]
  (let [previous-selected-place (:selected-place @app-state)]
    (cond
      previous-selected-place
      (swap! app-state
             l/move-card-place-cards-to
             previous-selected-place
             target-card-place)

      (or (= :stock target-card-place)
          (empty? (get @app-state target-card-place)))
      (swap! app-state assoc-in [:selected-place] nil)

      ;; valid card place?
      ((set l/card-places) target-card-place)
      (swap! app-state assoc-in [:selected-place] target-card-place))))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn reset-stock! [app-state]
  (swap! app-state l/reset-stock))
