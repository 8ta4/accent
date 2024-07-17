(ns alignment)

(defn align
  [x y]
;; TODO: Implement alignment
  (cons (repeat (inc (count x)) {:score 0}) (repeat (count y) (cons {:score 0} (repeat (count x) {}))))
  [])
