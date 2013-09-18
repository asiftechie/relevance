(ns relevance.bayes
  (:require [relevance.core :as core]))

(def categories (ref {}))

(def wc (ref {}))

(defn update-word-counts [category text]
  (dosync 
   (ref-set wc
    (reduce (fn [m k] 
              (update-in m [k] (fnil inc 0))) @wc
                (seq (core/tokenize text))))))

(defn update-category [category]
  (dosync 
    (alter categories assoc category 
      (inc (get @categories category 0)))))

;; We can do smarter things here like
;; filter only nouns and verbs and remove the stop words
(defn clean-training-corpus [corpus]
  "Wrap the crap"
  (->> corpus core/get-sentences))

(defn train [text]
  (let [text (slurp text)
        sentences (core/get-sentences text)]
  (map (partial update-word-counts "python") sentences)))

(defn test [] (train "training-data/python"))
