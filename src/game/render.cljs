(ns game.render)

(def canvas (.. js/document (getElementById "canvas")))
(def ctx (.getContext canvas "2d"))

(defn clear []
  (.clearRect ctx 0 0 9001 9001))