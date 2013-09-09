# Relevance

A library for ranking and sorting texts by relevance to any number of search terms
using Open NLP

## Usage

```clojure

;; (relevance terms documents)

(def skills ["python" "SQL" "django"])

(relevance skills
  {:a "Python and Django developers required for startups building web apps with Python." 
   :b "Java developer wanted. Python experience optional. We use Spring and the JVM"
   :c "Bar tender wanted. No experience necessary. Glass collecting and free drinks"})

```

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
