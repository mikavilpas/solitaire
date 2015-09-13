(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]
            [cljs.core.async :refer [chan put! timeout <! close! alts!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

;; this is the state that the game logic handles and "modifies" by
;; returning a new state
(defonce app-state (atom (l/new-game-state)))

;; The newest value is at the front of the list
(defonce state-history (atom (list)))
(defonce game-chan (chan))

(defn swap-with-history! [app-state & args]
  (let [old-state @app-state]
    (apply swap! app-state args)
    (swap! state-history #(take 10 (conj % old-state)))))

(defn turn-card! [app-state card card-place-name]
  (swap-with-history! app-state l/turn-card card card-place-name))

(defn deselect! [app-state]
  (swap! app-state assoc :selected-place nil))

(defn select-or-move! [app-state target-card-place]
  (print "select-or-move! " target-card-place)
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

(defn show-hint! [app-state]
  (let [hints (l/get-hints @app-state
                           (:selected-place @app-state))]
    ;; TODO show hints using core.async
    (print "show-hint!: " hints)))

(defn init-game-loop
  "Inits the loop in the background and returns a function that stops it"
  []
  (let [stopping-channel (chan)]
    (go-loop []
      (let [[value channel] (alts! [game-chan stopping-channel])]
        (if (= channel stopping-channel)
          ;; this is called when figwheel reloads the code
          "stopping"

          (let [[event-name data] value]
            (condp = event-name
              :show-hint (show-hint! app-state)
              (print "Warning: unknown event " event-name))
            (recur)))))

    (fn stop! [] (put! stopping-channel :stop-game-loop))))

;; development time convenience follows, this could be moved to another file

;; contains a shutdown function of no arguments
(defonce system (atom (init-game-loop)))

(defn start-system []
  (reset! system (init-game-loop)))

(defn stop-system []
  (if-let [stop-system! @system]
    (stop-system!)
    ;; this is weird behaviour, warn about it
    (print "cannot stop system as it's not started")))

(defn restart-system []
  (stop-system)
  (start-system))
