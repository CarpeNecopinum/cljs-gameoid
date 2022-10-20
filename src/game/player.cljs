(ns game.player
  (:require [game.input :as input]
            [game.sprites :as sprites]))


(defonce alex_run (sprites/load-image "alex_run" "resources/alex_run.png"))
(defonce run_frames 6)
(defonce anim_speed 0.1)
(defonce run_speed 2)

;;; update the :dir component based on the pressed keys
;;; count up the :frame component when a direction key is pressed
;;; reset the :frame component when it reaches the end of the sprite sheet

(defn update-player [player]
  (let [{:keys [frame x y] :or {frame 0}} player
        [dir running] (cond
                        (input/key-pressed? 37) [:left true]
                        (input/key-pressed? 39) [:right true]
                        (input/key-pressed? 38) [:up true]
                        (input/key-pressed? 40) [:down true]
                        :else [(get player :dir :down) false])
        frame (if running (mod (+ frame anim_speed) run_frames) 0)
        [x y] (if running (case dir
                            :left [(- x run_speed) y]
                            :right [(+ x run_speed) y]
                            :up [x (- y run_speed)]
                            :down [x (+ y run_speed)])
                  [x y])
        iframe (int frame)
        sprite-col (case dir :right 0 :up 6 :left 12 :down 18)
        sprite {:image alex_run
                :row 0
                :col (+ sprite-col iframe)
                :width 16
                :height 32
                :zoom 2}]
    (assoc player :dir dir :frame frame :x x :y y :sprites [sprite])))

;;; go through all entities in the world and move the ones that have the :player component according to pressed-keys
(defn update-players [world]
  (->> world
       (map (fn [entity]
              (if (:player entity)
                (update-player entity)
                entity)))))