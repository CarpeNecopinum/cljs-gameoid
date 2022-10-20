(ns game.input)

(def element (.. js/document (getElementById "canvas")))

(def keys-pressed (atom #{}))

(defn key-pressed? [key]
  (contains? @keys-pressed key))

(set! (.. js/window -onkeydown)
      (fn [e]
        (swap! keys-pressed conj (.-keyCode e))))

(set! (.. js/window -onkeyup)
      (fn [e]
        (swap! keys-pressed disj (.-keyCode e))))
