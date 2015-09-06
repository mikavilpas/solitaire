(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn select-or-move! [app-state target-card-place]
  (let [target-card (last (get @app-state target-card-place))
        previous-selected-place (:selected-place @app-state)]
    (cond
      (not (:facing-up target-card))
      (turn-card! app-state target-card target-card-place)

      (or (= :stock target-card-place)
          (empty? (get @app-state target-card-place)))
      (swap! app-state assoc-in [:selected-place] nil)

      previous-selected-place
      (swap! app-state
             l/move-card-place-cards-to
             previous-selected-place
             target-card-place)

      ;; valid card place?
      ((set l/card-places) target-card-place)
      (swap! app-state assoc-in [:selected-place] target-card-place))))

(defn reset-stock! [app-state]
  (swap! app-state l/reset-stock))

(defn deselect! [app-state]
  (swap! app-state assoc :selected-place nil))
