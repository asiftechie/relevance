(ns relevance.core
  (:use [opennlp.nlp]
        [opennlp.tools.filters]
        [opennlp.treebank]))

;; Tokenizers. Models are loaded from disk

(def get-sentences 
  (make-sentence-detector "models/en-sent.bin"))

(def tokenize 
  (make-tokenizer "models/en-token.bin"))

(def pos-tag 
  (make-pos-tagger "models/en-pos-maxent.bin"))

(defn contains-term?
  "Given a sentence, check if a term exist in that sentence"
  [sentence term]
  (let [tokens (tokenize sentence)]
    (boolean (some #{term} tokens))))

(defn extract-sentance-with-term [sentences term]
  (->> sentences
       (filter (fn [sent] (contains-term? sent term)))))
  