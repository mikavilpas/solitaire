(ns solitaire.view.actions
  (:require [solitaire.core.logic :as l]
            [clojure.set :as set]
            [cljs.core.async :refer [chan put! timeout <! close! alts!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

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

(defn auto-move! [app-state card-place-name]
  (swap-with-history! app-state l/auto-move card-place-name))

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

(defn show-hint! [app-state ui-state]
  (let [hinted-places (l/get-hints @app-state
                                   (:selected-place @app-state))]
    (go (swap! ui-state assoc :hinted-card-places hinted-places)
        (<! (timeout 2000))
        (swap! ui-state assoc :hinted-card-places #{}))))

(defn init-game-loop
  "Inits the loop in the background and returns a function that stops it"
  [app-state-atom]
  (let [stopping-channel (chan)]
    (go-loop []
      (let [[value channel] (alts! [game-chan stopping-channel])]
        (if (= channel stopping-channel)
          ;; this is called when figwheel reloads the code
          "stopping"

          (let [[event-name & args] value
                handlers {:show-hint show-hint!
                          :reset-stock reset-stock!
                          :turn-card turn-card!
                          :deselect deselect!
                          :new-game new-game!
                          :select-or-move select-or-move!
                          :undo undo!
                          :auto-move auto-move!}
                handler (get handlers event-name
                             #(print "Warning: unknown event " event-name))]
            (try
              (apply handler args)
              (catch js/Error e
                (print "Exception: " (.-message e))
                (print (.-stack e))))
            (recur)))))

    (fn stop! [] (put! stopping-channel :stop-game-loop))))

