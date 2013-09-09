# Relevance

A library for quickly sorting texts by relevance to any number of search terms

## Usage

```clojure

;; (relevance terms documents)

(relevance ["python" "django"] 
  {:a "Python and Django developers required for startups building web apps with Python." 
   :b "Java developer wanted. Python experience optional. We use Spring and the JVM"})

```

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
