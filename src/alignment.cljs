(ns alignment)

(defn align
  [x y]
;; TODO: Implement alignment
  (cons (cons {:score 0} (map (fn [x*] {:score 0 :trace [0 x*]}) (range (count x))))
        (map (fn [y*] (cons {:score 0 :trace [y* 0]} (repeat (count x) {}))) (range (count y))))
  [])
