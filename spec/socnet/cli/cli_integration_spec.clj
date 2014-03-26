(ns socnet.cli.cli-integration-spec
  (:require [speclj.core :refer :all]
            [clojure.string :as s]
            [clj-time.core :as t]
            [socnet.cli.main :refer :all]))

(defn run-with-input
  [& lines]
  "Given a set of input lines, feed them into stdin
   with an exit command appended, and return whatever
   gets written to stdout"
  (let [a (atom (concat lines ["exit"]))]
    (with-out-str
      (with-redefs [read-line 
                    #(let [res (first @a)]
                       (swap! a rest)
                       (Thread/sleep 10)
                       res)]
        (-main)))))

(defn filter-output-lines
  [o]
  "Clean up stdout output to remove prompt symbols"
  (->
    (s/replace o prompt "")
    (s/split #"\n")))

(describe "posted message"
  (it "can be created and read back"
      (should= "hello everyone (moments from now)"
               (->
                 (run-with-input "joe -> hello everyone" "joe")
                 filter-output-lines first)))
  (it "displays elapsed time"
      (with-redefs [t/now (constantly (-> 36 t/hours t/from-now))]
        (should= "hola (1 day ago)"
                 (->
                   (run-with-input "joe -> hola" "joe")
                   filter-output-lines first)))))

(describe "following other users"
  (it "makes followed users' posts available on own wall"
      (should= "poe - stupidity is a talent for misconception (moments from now)"
               (->
                 (run-with-input "joe follows poe"
                                 "poe -> stupidity is a talent for misconception"
                                 "joe wall")
                 filter-output-lines first))))

(describe "samples from exercise description"
   (it "should produce expected outputs (without times)"
       (should= ["I love the weather today (moments from now)"
                 "Good game though. (moments from now)"
                 "Damn! We lost! (moments from now)"
                 "Charlie - I'm in New York today! Anyone wants to have a coffee? (moments from now)" 
                 "Alice - I love the weather today (moments from now)"
                 "Charlie - I'm in New York today! Anyone wants to have a coffee? (moments from now)" 
                 "Bob - Good game though. (moments from now)"
                 "Bob - Damn! We lost! (moments from now)"
                 "Alice - I love the weather today (moments from now)"]
                (->
                  (run-with-input "Alice -> I love the weather today"
                                  "Bob -> Damn! We lost!"
                                  "Bob -> Good game though."
                                  "Alice"
                                  "Bob"
                                  "Charlie -> I'm in New York today! Anyone wants to have a coffee?"
                                  "Charlie follows Alice"
                                  "Charlie wall"
                                  "Charlie follows Bob"
                                  "Charlie wall")
                    filter-output-lines))))

(run-specs)