(ns alignment-test
  (:require [alignment]
            [clojure.test :refer [deftest is]]))

(deftest alignment
  (is (= (alignment/align [] []) []))
  (is (= (alignment/align [:a] []) [[:a nil]]))
  (is (= (alignment/align [] [:a]) [[nil :a]])))
