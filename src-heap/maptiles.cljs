(ns game.maptiles
  (:require [game.sprites :as sprites]
            [game.arr2d :as td]
            [game.render :as render]))

(def tileset (sprites/load-image "tileset" "resources/tileset.png"))
(defn tile-at [row col] {:image tileset :row row :col col :width 16 :height 16 :zoom 2})

(def tile-grass (tile-at 0 1))
(def tile-dirt (tile-at 0 2))
(def tile-path (tile-at 0 3))
(def tile-water (tile-at 0 4))

(def block-size 64) ;;; number of tiles per axis per block
(def block-width (* block-size 16)) ;;; number of pixels per axis per block

(defn frac [x] (- x (int x)))

(defn noise
  "Generate a random number between 0 and 1 for the given x and y coordinates"
  [x y seed]
  (let [n (+ (* x x 0.01) (* y y 0.01) seed x y)]
    (frac (+ (Math/sin n) (Math/cos (/ x 1.345)) (Math/sin (/ y 0.853))))))

(defn fractal-noise [x y base-seed]
  (+ (noise x y base-seed)
     (/ (noise (* 2 x) (* 2 y) base-seed) 2)
     (* (noise (/ x 4) (/ y 4) base-seed) 2)))

;;; use wavefunction collapse to generate a map
(def all-types [:water, :dirt, :grass, :plateau])

(defn type-for [seed x y]
  (let [n (fractal-noise (/ x 16) (/ y 16) seed)
        n (/ n 3)]
    (cond
      (< n -0.25) :water
      (< n 0.25) :dirt
      (< n 0.5) :grass
      :else :plateau)))

(defn block-types [seed x y]
  (let [x (* x block-size)
        y (* y block-size)]
    (td/arr2d block-size block-size
              (fn [row col] (type-for seed (+ x col) (+ y row))))))

(defn tile-for [type]
  (case type
    :water tile-water
    :dirt tile-dirt
    :grass tile-grass
    :plateau tile-path))

(def block-canvas (new js/OffscreenCanvas block-width block-width))
(def block-ctx (.getContext block-canvas "2d"))

(defn prerender-block [tiles]
  (set! (.-fillStyle block-ctx) "blue")
  (.fillRect block-ctx 0 0 block-width block-width)
  (doseq [[y row] (map vector (range) tiles)
          [x tile] (map vector (range) row)]
    (let [tile (tile-for tile)
          {:keys [image row col]} tile
          sx (* col 16)
          sy (* row 16)
          dx (* x 16)
          dy (* y 16)
          dim 16]
      (.drawImage block-ctx image sx sy dim dim dx dy dim dim)))
  (.transferToImageBitmap block-canvas))

(def start-block (prerender-block (block-types 4 0 0)))
(.drawImage render/ctx start-block 0 0 block-width block-width 0 0 block-width block-width)
