(ns game.sprites
  (:require [game.render :as render]))

(defonce images (atom {}))

;;; Sprite [:image :row :col :width :height]

(defn draw-sprite [x y sprite]
  (let [{:keys [image row col width height zoom] :or {zoom 1}} sprite
        image (if (string? image) (get @images image) image)
        sx (* col width)
        sy (* row height)]
    (.drawImage render/ctx image sx sy width height x y (* zoom width) (* zoom height))))

(defn load-image [symbol url]
  (let [img (new js/Image)]
    (set! (.. img -src) url)
    (swap! images assoc symbol img)
    img))

(defn render-sprites [world]
  (set! (.. render/ctx -imageSmoothingEnabled) false)
  (doseq [entity world]
    (when-let [sprites (:sprites entity)]
      (doseq [sprite sprites]
        (let [{:keys [x y]} entity]
          (draw-sprite x y sprite))))))