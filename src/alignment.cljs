(ns alignment
  (:require [com.rpl.specter :as specter]))

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

(defn finalize*
  [[i j] matrix]
  (if (:score (llast matrix))
    matrix
    (recur (if (= j (dec (count (first matrix))))
             [(inc i) 1]
             [i (inc j)])
           (set-entry [i j] matrix))))

(def finalize
  (partial finalize* [1 1]))

(defn initialize
  [x y]
  (cons (cons {:score 0}
              (map (fn [j]
                     {:score 0 :previous [0 j]})
                   (range (count x))))
        (map (fn [i]
               (cons {:score 0 :previous [i 0]} (repeat (count x) {})))
             (range (count y)))))

(defn trace*
  [alignment [i j] x y matrix]
  (if (and (= i 0) (= j 0))
    alignment
    (let [[i* j* :as previous] (:previous (nth (nth matrix i) j))]
      (recur (cons (cond
                     (= i i*) [(nth x (dec j)) nil]
                     (= j j*) [nil (nth y (dec i))]
                     :else [(nth x (dec i)) (nth y (dec j))])
                   alignment)
             previous
             x
             y
             matrix))))

(defn trace
  [x y matrix]
  (trace* [] [(count y) (count x)] x y matrix))

(defn align
  [x y]
  (trace x y (finalize (initialize x y))))
