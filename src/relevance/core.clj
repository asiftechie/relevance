(ns relevance.core
  (:use [opennlp.nlp]
        [opennlp.tools.filters]
        [opennlp.treebank]))

;; Tokenizers. Models are loaded from disk

;; CC Coordinating conjunction
;; CD Cardinal number
;; DT Determiner
;; EX Existential there
;; FW Foreign word
;; IN Preposition or subordinating conjunction
;; JJ Adjective
;; JJR Adjective, comparative
;; JJS Adjective, superlative
;; LS List item marker
;; MD Modal
;; NN Noun, singular or mass
;; NNS Noun, plural
;; NNP Proper noun, singular
;; NNPS Proper noun, plural
;; PDT Predeterminer
;; POS Possessive ending
;; PRP Personal pronoun
;; PRP$ Possessive pronoun
;; RB Adverb
;; RBR Adverb, comparative
;; RBS Adverb, superlative
;; RP Particle
;; SYM Symbol
;; TO to
;; UH Interjection
;; VB Verb, base form
;; VBD Verb, past tense
;; VBG Verb, gerund or present participle
;; VBN Verb, past participle
;; VBP Verb, non­3rd person singular present
;; VBZ Verb, 3rd person singular present
;; WDT Wh­determiner
;; WP Wh­pronoun
;; WP$ Possessive wh­pronoun
;; WRB Wh­adverb

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
       (filter (fn [sentence] (contains-term? sentence term)))))
  
(defn pos-tag-sentences 
  ""
  [sentences]
  (map 
    (fn [sentence] 
      (pos-tag (tokenize sentence))) 
        sentences))

(def pos-tag-text (comp pos-tag-sentences get-sentences))

;; All these methods assume they are passed the result of a pos-ent tagger
;; i.e ["Word" "TAG"]
;; TODO make this use core typed or something to make it clearer

(def noun? (fn [[_ tag]] (= tag "NNS")))

(defn verb? 
  "Is this tagged item a verb form?"
  [[_ item]]
  (let [verb-tags #{"VB" "VBD" "VBG" "VBN" "VBP" "VBZ"}]
    (contains? verb-tags item)))

(defn adjective?
  "Is this tagged item an adjective?"
  [[_ item]]
  (let [adj-tags #{"JJ" "JJR" "JJS"}]
    (contains? adj-tags item)))

(defn extract-tag [sentences tag]
  (let [tags (pos-tag-sentences sentences)]
    tags))

;;    (map #(filter (fn [_ t] (= t tag)) %) tags)))
