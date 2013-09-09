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

(defn pos-tag-text [text]
  (let [sentences (get-sentences text)]
    (pos-tag-sentences sentences)))

(def noun? (fn [[_ tag]] (= tag "NNS")))

(defn extract-tag [sentences tag]
  (let [tags (pos-tag-sentences sentences)]
    tags))

;;    (map #(filter (fn [_ t] (= t tag)) %) tags)))
