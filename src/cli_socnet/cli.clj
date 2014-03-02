(ns cli-socnet.cli
    (:gen-class)
    (:require [cli-socnet.core :as c] 
              [clojure.string :as s]
              [clj-time.coerce :as tc])
    (:import (com.ocpsoft.pretty.time PrettyTime)
             (java.util Date)))

(def ^:const prompt "> ")

(defn print-post
  [p]
  "Just a function for now, may become a protocol extended across
   various types we need to print beyond User"
  (println (str (:username p) " - " (:message p) " ("
                (.format (PrettyTime. (tc/to-date (:timestamp p)))
                  (Date.)) ")")))

(defn cmd-type
  [_ c]
  (let [[_ op] (s/split c #"\s" 3)] 
    (if op (keyword op) :read)))

(defmulti exec cmd-type)

(defmethod exec :->
  [socnet c]
  (let [[username _ msg] (s/split c #"\s+" 3)]
    (c/post socnet username msg)))
    
(defmethod exec :read
  [socnet username]
  (mapv print-post (c/read-posts socnet username))
  socnet)

(defmethod exec :follows
  [socnet c]
  (let [[username _ followee] (s/split c #"\s+")]
    (c/follow socnet username followee)))

(defmethod exec :wall
  [socnet c]
  (let [[username] (s/split c #"\s+")]
    (mapv print-post (c/wall socnet username)))
  socnet)

(defn -main
  [& args]
  (loop [users (c/create)]
    (print prompt)
    (flush)
    (let [l (read-line)]
      (when (not= l "exit")
        (recur (exec users l))))))