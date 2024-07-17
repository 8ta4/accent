(ns alignment-test
  (:require [alignment]
            [clojure.test :refer [deftest is]]))

(deftest alignment
  (is (= (alignment/align [] []) [])))