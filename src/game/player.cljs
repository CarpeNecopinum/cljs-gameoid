(ns game.player
  (:require [game.input :as input]
            [game.sprites :as sprites]))


(defonce alex_run (sprites/load-image "alex_run" "resources/alex_run.png"))
(defonce run_frames 6)
(defonce anim_speed 0.1)
(defonce run_speed 2)

(defn update-player [player]
  (let [{:keys [frame x y dir] :or {frame 0, dir :down}} player
        xdir (cond (input/key-pressed? 37) :left
                   (input/key-pressed? 39) :right
                   :else :none)
        ydir (cond (input/key-pressed? 38) :up
                   (input/key-pressed? 40) :down
                   :else :none)
        [dir running] (cond
                        (not= ydir :none) [ydir true]
                        (not= xdir :none) [xdir true]
                        :else [dir false])
        frame (if running (mod (+ frame anim_speed) run_frames) 0)
        run_speed (if (and (not= xdir :none) (not= ydir :none))
                    (/ run_speed 1.414)
                    run_speed)
        x (cond (= xdir :left) (- x run_speed)
                (= xdir :right) (+ x run_speed)
                :else x)
        y (cond (= ydir :up) (- y run_speed)
                (= ydir :down) (+ y run_speed)
                :else y)
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