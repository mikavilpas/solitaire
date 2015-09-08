(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]))

;; The newest value is at the front of the list
(defonce state-history (atom (list)))
(defonce history-size 10)

(defn swap-with-history! [app-state & args]
  (let [old-state @app-state]
    (apply swap! app-state args)
    (swap! state-history #(take history-size (conj % old-state)))))

(defn turn-card! [app-state card card-place-name]
  (swap-with-history! app-state l/turn-card card card-place-name))

(defn deselect! [app-state]
  (swap! app-state assoc :selected-place nil))

(defn select-or-move! [app-state target-card-place]
  (let [target-card (last (get @app-state target-card-place))
        previous-selected-place (:selected-place @app-state)]
    (cond
      previous-selected-place
      (swap-with-history! app-state
                          l/move-card-place-cards-to
                          previous-selected-place
                          target-card-place)

      (and target-card
           (not (:facing-up target-card)))
      (turn-card! app-state target-card target-card-place)

      (or (= :stock target-card-place)
          (empty? (get @app-state target-card-place)))
      (deselect! app-state)

      ;; valid card place?
      ((set l/card-places) target-card-place)
      (swap! app-state assoc-in [:selected-place] target-card-place))))

(defn reset-stock! [app-state]
  (swap-with-history! app-state l/reset-stock))

(defn new-game! [app-state]
  (reset! app-state (l/new-game-state))
  (reset! state-history []))

(defn undo! [app-state]
  (when (not-empty @state-history)
    (reset! app-state (first @state-history))
    (swap! state-history #(drop 1 %))))
