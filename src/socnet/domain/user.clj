(ns socnet.domain.user
  (:require [clj-time.core :as t]))

(defrecord User [username following])

(defprotocol UserRepo
  (create-user-if-absent [repo username])
  (lookup-user [repo username])
  (update-user [repo user]
               "The api doesn't capture the semantics of concurrency"))

(defn create
  [username]
 (User. username #{}))

(defn follow
  [user followee]
  (update-in user [:following] conj followee))

(defn get-wall-usernames
  [user]
  (-> user
    :following
    (conj (:username user))))
  