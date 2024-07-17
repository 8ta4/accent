(ns alignment-test
  (:require [alignment]
            [clojure.test :refer [deftest is]]))

(deftest alignment
  (is (= (alignment/align [] []) []))
  (is (= (alignment/align [:a] []) [[:a nil]]))
  (is (= (alignment/align [] [:a]) [[nil :a]]))
  (is (= (alignment/align [:a] [:a]) [[:a :a]]))
  (is (= (alignment/align [:a :a] []) [[:a nil] [:a nil]]))
  (is (= (alignment/align [:a :b] [:a]) [[:a :a] [:b nil]]))
  (is (= (alignment/align [:a :b] [:b]) [[:a nil] [:b :b]])))
