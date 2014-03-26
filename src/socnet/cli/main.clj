(ns socnet.cli.main
  (:gen-class)
  (:require [socnet.cli.commands :refer :all]
            [socnet.infra.in-memory-user-repo :as ur]
            [socnet.infra.in-memory-post-repo :as pr]))

(def ^:const prompt "> ")

(defn system
  []
  {:user-repo (ur/create)
   :post-repo (pr/create)})

(defn -main
  [& args]
  (let [system (system)]
    (loop []
      (print prompt)
      (flush)
      (let [l (read-line)]
        (when (not= l "exit")
          (exec system l)
          (recur))))))