(ns socnet.cli.commands
  (:require [clojure.string :as s]
            [socnet.app.socnet-service :as svc]
            [socnet.cli.prettyprint :as cpp]))

(defmulti exec (fn [_ line]
                 (-> (s/split line #"\s+" 3)
                   second)))

(defmethod exec "->"
  [system line]
  (let [[username _ msg] (s/split line #"\s+" 3)]
    (svc/add-post system username msg)))
    
(defmethod exec nil
  [system username]
  (mapv #(cpp/print-post % false)
        (svc/get-posts-for-user system username)))

(defmethod exec "follows"
  [system line]
  (let [[username _ followee] (s/split line #"\s+")]
    (svc/follow-user system username followee)))

(defmethod exec "wall"
  [system line]
  (let [[username] (s/split line #"\s+")]
    (mapv cpp/print-post (svc/get-wall-posts-for-user system username))))