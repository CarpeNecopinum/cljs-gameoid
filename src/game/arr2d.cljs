(ns game.arr2d)

(defn build [width height fn]
  (vec (for [y (range height)]
         (vec (for [x (range width)]
                (fn x y))))))


