(ns alignment)

(defn align
  [x y]
;; TODO: Implement alignment
  (cons (cons {:score 0}
              (map (fn [j]
                     {:score 0 :trace [0 j]})
                   (range (count x))))
        (map (fn [i]
               (cons {:score 0 :trace [i 0]} (repeat (count x) {})))
             (range (count y))))
  [])
