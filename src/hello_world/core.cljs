(ns hello-world.core)

(def canvas (js/document.getElementById "canvas"))
(def ctx (.getContext canvas "2d"))

(defn draw-circle [x y r color]
  (set! (.-fillStyle ctx) color)
  (set! (.-strokeStyle ctx) "white")
  (set! (.-lineWidth ctx) (/ r 5))
  (.beginPath ctx)
  (.arc ctx x y r 0 (* 2 Math/PI))
  (.fill ctx)
  (.stroke ctx))

(defn clear []
  (.clearRect ctx 0 0 9001 9001))

(defn random-color []
  (str "rgb("
       (rand-int 255) ","
       (rand-int 255) ","
       (rand-int 255) ")"))

(defn random-sphere [width height]
  (let [r (+ 20 (rand-int 30))
        x (rand-int (- width r))
        y (rand-int (- height r))
        vx (- (rand-int 20) 10)
        vy (- (rand-int 20) 10)
        color (random-color)]
    {:x x :y y :r r :vx vx :vy vy :color color}))

(defn game-init []
  (let [width (.-width canvas)
        height (.-height canvas)
        circles (map (fn [_] (random-sphere width height)) (range 5))]
    {:width width :height height :circles circles :gy 1}))

(def *state (atom nil))
(reset! *state (game-init))

(set! (.. js/window -onresize)
      (fn []
        (let [width (.. js/window -innerWidth)
              height (.. js/window -innerHeight)]
          (set! (.. canvas -width) width)
          (set! (.. canvas -height) height)
          (swap! *state assoc :width width :height height))))


(defn game-update [state]
  (let [{:keys [width height circles gy]} state
        circles (map (fn [circle]
                       (let [{:keys [x y r vx vy]} circle
                             x (+ x vx)
                             y (+ y vy)
                             vx (if (< x r) (Math/abs vx) vx)
                             vx (if (> x (- width r)) (- (Math/abs vx)) vx)
                             vy (if (< y r)
                                  (Math/abs vy)
                                  (if (> y (- height r))
                                    (- (Math/abs vy))
                                    (+ vy gy)))]
                         (assoc circle :x x :y y :vx vx :vy vy)))
                     circles)]
    (assoc state :circles circles)))

(defn game-render [state]
  (clear)
  (doseq [{:keys [x y r color]} (:circles state)]
    (draw-circle x y r color)))

(defn game-loop []
  (swap! *state game-update)
  (game-render @*state)
  (js/requestAnimationFrame game-loop))
(game-loop)
