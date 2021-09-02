test:
    clojure -M:test:cognitect

test-focus:
    clojure -M:test:cognitect -i :focus

kaocha:
    clojure -M:test:kaocha

watch:
    clojure -M:test:kaocha --watch

repl:
    clojure -M:test:repl/rebel
