(ns alignment
  (:require [com.rpl.specter :as specter]))

(defn trace
  [alignment [i j] x y matrix]
  (if (and (= i 0) (= j 0))
    alignment
    (let [[i* j* :as previous] (:previous (nth (nth matrix i) j))]
      (recur (cond
               (= i i*) (cons [(nth x (dec j)) nil] alignment)
               (= j j*) (cons [nil (nth y (dec i))] alignment)
               :else (cons [(nth x (dec i)) (nth y (dec j))] alignment))
             previous
             x
             y
             matrix))))

(def llast
  (comp last last))

(defn set-previous
  [[i j :as previous] matrix]
  (specter/setval :previous previous (nth (nth matrix i) j)))

(defn set-entry
  [[i j] matrix]
  (specter/setval [(specter/nthpath i j)]
                  (max-key :score
                           (specter/transform :score inc (set-previous [(dec i) (dec j)] matrix))
                           (set-previous [(dec i) j] matrix)
                           (set-previous [i (dec j)] matrix))
                  matrix))

(defn set-entries
  [matrix]
  (if (:score (llast matrix))
    matrix
    (set-entry [1 1] matrix)))

(defn align
  [x y]
;; TODO: Implement alignment
  (trace []
         [(count y)
          (count x)]
         x
         y
         (set-entries (cons (cons {:score 0}
                                  (map (fn [j]
                                         {:score 0 :previous [0 j]})
                                       (range (count x))))
                            (map (fn [i]
                                   (cons {:score 0 :previous [i 0]} (repeat (count x) {})))
                                 (range (count y)))))))
