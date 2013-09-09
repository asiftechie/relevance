(ns relevance.core
  (:require [clojure.string :as str])
  (:use [opennlp.nlp]
        [opennlp.tools.filters]
        [opennlp.treebank]))

;; Tokenizers. Models are loaded from disk

(def get-sentences 
  (make-sentence-detector "models/en-sent.bin"))

(defn format-tokens 
  "Normalize everything to lowercase"
  [tokens]
  (map #(.toLowerCase %) tokens))

(def tokenize
  (comp format-tokens
        (make-tokenizer "models/en-token.bin")))

(defn n-grams [n text]
  (map (partial str/join " ")
    (partition n (tokenize text))))

(def bi-grams (partial n-grams 2))
(def tri-grams (partial n-grams 3))

(def pos-tag 
  (make-pos-tagger "models/en-pos-maxent.bin"))

(defn contains-term?
  "Given a sentence, check if a term exist in that sentence"
  [sentence term]
  (let [tokens (tokenize sentence)]
    (boolean (some #{term} tokens))))

(defn extract-sentance-with-term [sentences term]
  (->> sentences
       (filter (fn [sentence] (contains-term? sentence term)))))
  
(defn pos-tag-sentences 
  ""
  [sentences]
  (map 
    (fn [sentence] 
      (pos-tag (tokenize sentence))) 
        sentences))

(def pos-tag-text (comp pos-tag-sentences get-sentences))

;; Filters for tagged items in the form [ITEM TAG]
;; ******************************************************************

(defn noun?
  [[_ tag]]
  (let [noun-tags #{"NN" "NNS" "NNP" "NNPS"}]
    (contains? noun-tags tag)))

(defn verb? 
  "Is this tagged item a verb form?"
  [[_ tag]]
  (let [verb-tags #{"VB" "VBD" "VBG" "VBN" "VBP" "VBZ"}]
    (contains? verb-tags tag)))

(defn adjective?
  "Is this tagged item an adjective?"
  [[_ tag]]
  (let [adj-tags #{"JJ" "JJR" "JJS"}]
    (contains? adj-tags tag)))

(defn noun-or-verb? [v] (or (noun? v) (verb? v)))

(defn extract-predicate [predicate sentences]
  (->> (pos-tag-sentences sentences)
       (map (partial filter predicate))))

;; Methods for extracting terms from text
;; ******************************************************************

(def extract-nouns-from-sentences (partial extract-predicate noun?))
(def extract-verbs-from-sentences (partial extract-predicate verb?))
(def extract-adjectives-from-sentences (partial extract-predicate adjective?))
(def extract-noun-or-verb-from-sentences (partial extract-predicate noun-or-verb?))

(defn extract-from-text
  "Extract items from a text 
   i.e extract all nouns or all verbs from a text"
  [text predicate]
  (extract-predicate predicate (get-sentences text)))

(def extract-nouns-from-text (partial extract-from-text noun?))
(def extract-verbs-from-text (partial extract-from-text verb?))
(def extract-adjectives-from-text (partial extract-from-text adjective?))
(def extract-noun-or-verb-from-text (partial extract-from-text noun-or-verb?))

;; Useful methods

(defn term-frequency 
  "Given a set of tokens and a term return the number of times
   the term appears in the tokens"
  [term tokens]
  (reduce 
    (fn [acc v] (if (= term v) (inc acc) acc)) 0 tokens))

(def terms ["Java" "Scala" "JVM"])
    