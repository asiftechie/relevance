
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
  (mapv (partial str/join " ")
    (partition n (tokenize text))))

(def bi-grams (partial n-grams 2))

(def tri-grams (partial n-grams 3))

;; Just word chars i.e strip out the crap
(def words 
  (let [default-word-expr #"[A-Za-z0-9]"]
    (partial re-seq default-word-expr)))

(defn canonicalize 
  "Return a canonical text 
     -> only lowercase letters, numbers and spaces"
  [text]
  (clojure.string/join " " 
    (->> (words text)
         (map #(.toLowerCase %)))))

(def pos-tag 
  (make-pos-tagger "models/en-pos-maxent.bin"))

(defn contains-term?
  "Given a sentence, check if a term exist in that sentence"
  [sentence term]
  (let [tokens (tokenize sentence)]
    (boolean (some #{term} tokens))))

(defn extract-sentance-with-term [sentences term]
  (->> sentences
       (filter #(contains-term? % term))))
  
(defn pos-tag-sentences 
  ""
  [sentences]
  (mapv 
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
    (fn [acc v] 
      (if (= term v) 
        (inc acc) 
        acc)) 0 tokens))

(defn top-n-terms 
  "Extracts the top n terms from a given text"
  [n text]
  (letfn [(to-count [[k v]] [k (count v)])]
    (let [words (tokenize text)]
      (->> words 
         (group-by identity)
         (map to-count)
         (sort-by second)
         (reverse)
         (take n)))))

(def key-words (partial top-n-terms 10))

(defn term-sentence-extractor 
  "Given a text and a set of tags, pull out any
   sentences that contain one or more matching tags"
  [text terms]
  (let [sentences (get-sentences text)]
    (filter #(re-find (re-pattern 
                        (apply str 
                          (interpose "|" terms))) %) sentences)))
