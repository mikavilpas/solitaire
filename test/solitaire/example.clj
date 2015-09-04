(ns solitaire.example)

;; literal data

1
3e5
3/2
"hello world!"
[1 2 3 "hello"]

;; symbolic data
(def best-user "asd")
best-user
(symbol? 'best-user)
:this-is-a-keyword

'(1 2 3 "hello")
'hello





;; unquoted list is a function call
(= 1 2) ; 1 = 2
(range 5)




;; null == nil
nil



;; functions
(defn hello [name]
  (str
   "hello " name "!"))

(hello "mage")



;; collections:

;; map ("dictionary")
{:name "Clojure"}
{:name "Clojure", :time "now", 3 "anything"}

(:name {:name "Clojure"})
;; new IDictionary<string,string>("name", "Clojure")["name"]

;; set
(set [1 2 3])

(contains? #{1 2 3} 3)
(#{1 2 3} 3)
(#{1 2 3} 4)


;; let creates "variables" (temporary bindings), like "using" in C#
(let [a 1, b 2]
  (> a b))

;; a macro defines new syntax
(defmacro comment [& arguments] nil)
;; params string[] arguments

(comment (dangerous unbound code!!! lolo)
         (i can write anything here))

;; using things from the host runtime environment
;; (java libraries or the DOM)
(comment

  (System/nanoTime)

  (import java.util.Calendar)
  (import java.util.Date)
  (new Date)
  (Date.)
  (Calendar/getInstance))
