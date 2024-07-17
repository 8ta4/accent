(ns alignment)

(defn trace
  [alignment [i j] matrix]
  (if (and (= i 0) (= j 0))
    alignment
    (recur alignment (:trace (nth (nth matrix i) j)) matrix)))

(defn align
  [x y]
;; TODO: Implement alignment
  (trace []
         [(count y)
          (count x)]
         (cons (cons {:score 0}
                     (map (fn [j]
                            {:score 0 :trace [0 j]})
                          (range (count x))))
               (map (fn [i]
                      (cons {:score 0 :trace [i 0]} (repeat (count x) {})))
                    (range (count y))))))
