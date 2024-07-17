(ns alignment)

(defn trace
  [alignment [i j] x y matrix]
  (if (and (= i 0) (= j 0))
    alignment
    (let [[i* j* :as previous] (:previous (nth (nth matrix i) j))]
      (recur (cond
               (= i i*) (cons [(nth x i) nil] alignment)
               (= j j*) (cons [nil (nth y j)] alignment)
               :else alignment)
             previous
             x
             y
             matrix))))

(def llast
  (comp last last))

(defn align
  [x y]
;; TODO: Implement alignment
  (trace []
         [(count y)
          (count x)]
         x
         y
         (cons (cons {:score 0}
                     (map (fn [j]
                            {:score 0 :previous [0 j]})
                          (range (count x))))
               (map (fn [i]
                      (cons {:score 0 :previous [i 0]} (repeat (count x) {})))
                    (range (count y))))))
