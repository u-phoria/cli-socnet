(ns cli-socnet.core
    (:require [clj-time.core :as t]))

(defrecord User [following posts])
(defrecord Post [username message timestamp])

(defprotocol SocNet
  (post [this username message])
  (read-posts [this username])
  (follow [this username followee])
  (wall [this username]))

(defrecord UserStore [users]
  SocNet
  (post
    [this username message]
    (update-in this [:users username :posts]
               #(conj (or % '())
                      (->Post username message (t/now)))))
  (read-posts
    [this username]
    (get-in this [:users username :posts]))
    
  (follow
    [this username followee]
    (update-in this [:users username :following]
               #(conj (or % #{}) followee)))
    
  (wall
    [this username]
    (let [followed-users (conj (get-in this [:users username :following]) username)
          followed-posts (mapcat #(get-in this [:users % :posts])
                                 followed-users)]
      (sort-by :timestamp #(compare %2 %1) followed-posts))))

(defn create [] (UserStore. {}))