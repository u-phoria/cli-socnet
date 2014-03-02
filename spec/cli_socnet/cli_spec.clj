(ns cli-socnet.cli-spec
  (:require [speclj.core :refer :all]
            [clojure.string :as s]
            [clj-time.core :as t]
            [cli-socnet.cli :refer :all]))

(defn run-with-input
  [& lines]
  (with-out-str
    (with-in-str (apply str (interpose \newline
                                       (concat lines ["exit"]))) 
      (-main))))

(defn filter-output-lines
  [o]
  (->
    (s/replace o prompt "")
    (s/split #"\n")))

(describe "posted message"
  (it "can be created and read back"
      (should= "joe - hello everyone (moments from now)"
               (->
                 (run-with-input "joe -> hello everyone" "joe")
                 filter-output-lines first)))
  (it "displays elapsed time"
      (with-redefs [t/now (constantly (-> 36 t/hours t/from-now))]
        (should= "joe - hola (1 day ago)"
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

(describe "sample requirement functionality"

(run-specs)