(ns solitaire.view.actions
  (:require [solitaire.logic :as l]))

(defn select-or-move-card!
  "card-id is e.g. ♥2, the card that was clicked"
  [card-id app-state card-place-name]
  (let [different-place-selected (and (:selected-place @app-state)
                                      (not (= (:selected-place @app-state)
                                              card-place-name)))]
    (if different-place-selected
      ;; (swap! app-state l/move-card card-id @app-state)
      (swap! app-state assoc-in [:selected-place] card-id))))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn reset-stock! [app-state]
  (swap! app-state l/reset-stock))
