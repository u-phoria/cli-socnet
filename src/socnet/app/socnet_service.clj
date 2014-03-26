(ns socnet.app.socnet-service
  (:require [socnet.domain [user :as u]
                           [post :as p]]))

(defn- get-or-create-user
  [user-repo username]
  (or (u/lookup-user user-repo username)
      (let [new-user (u/create username)]
        (u/create-user-if-absent user-repo new-user)
        new-user)))

(defn add-post
  [{:keys [user-repo post-repo]} username message]
  (get-or-create-user user-repo username)
  (->> (p/create-post username message)
    (p/add post-repo)))

(defn follow-user
  [{:keys [user-repo]} username followee]
  (-> (get-or-create-user user-repo username)
   (u/follow followee)
   (#(u/update-user user-repo %))))

(defn get-posts-for-user
  [{:keys [post-repo]} username]
  (p/get-posts-for-user post-repo username))

(defn get-wall-posts-for-user
  [{:keys [user-repo post-repo]} username]
  (-> (u/lookup-user user-repo username)
    u/get-wall-usernames
    (#(p/get-posts-for-users post-repo % :order :timestamp-desc))))