(ns alignment)

(defn trace
  [alignment [i j] matrix]
  (if (and (= i 0) (= j 0))
    alignment
    (let [[i* j* :as previous] (:previous (nth (nth matrix i) j))]
      (recur (cond
               (= i i*) alignment
               (= j j*) alignment
               :else alignment)
             previous
             matrix))))

(defn align
  [x y]
;; TODO: Implement alignment
  (trace []
         [(count y)
          (count x)]
         (cons (cons {:score 0}
                     (map (fn [j]
                            {:score 0 :previous [0 j]})
                          (range (count x))))
               (map (fn [i]
                      (cons {:score 0 :previous [i 0]} (repeat (count x) {})))
                    (range (count y))))))
