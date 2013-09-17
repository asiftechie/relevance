(ns relevance.bayes
  (:require [relevance.core :as core]))

(def categories (ref {}))

(def wc (ref {}))

(defn update-word-counts [category text]
  (dosync 
   (ref-set wc
    (reduce
      (fn [m k]
        (update-in m [k] 
          (fnil inc 0))) @wc
            (seq (core/tokenize text))))))
