(ns socnet.domain.post
  (:require [clj-time.core :as t]))

(defrecord Post [id username message timestamp])

(defprotocol PostRepo
  (add [repo post])
  (get-posts-for-user [repo username])
  (get-posts-for-users [repo usernames
                        & {:keys [order]
                           :or {order :timestamp-desc}}]))

(defn create-post
  [username message]
  (Post. nil username message (t/now)))