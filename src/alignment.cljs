(ns alignment)

(defn align
  [x y]
  (cons (repeat (inc (count x)) {:score 0}) (repeat (count y) (cons {:score 0} (repeat (count x) {}))))
  [])
