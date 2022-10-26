(ns game.core
  (:require [game.sprites :as sprites]
            [game.render :as render]
            [game.player :as player]))

(def *world (atom ()))

(swap! *world conj {:name "Player 1"
                    :player 1
                    :x 100
                    :y 100
                    :sprites []})


(defn game-update [world]
;;   (print input/keys-pressed)
  (->> world
       (player/update-players)))

(defn game-render [world]
  (render/clear)
  (sprites/render-sprites world))

(defn game-loop []
  (swap! *world game-update)
  (game-render @*world)
  (js/requestAnimationFrame game-loop)
  0)

(game-loop)