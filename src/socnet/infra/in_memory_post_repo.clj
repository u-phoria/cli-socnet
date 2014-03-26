(ns socnet.infra.in-memory-post-repo
  (:require [socnet.domain.post :refer :all]))

(defn create
  []
  (let [last-id (atom 0)
        repo (atom #{})]
    (reify PostRepo
      
      (add [_ post]
        (swap! repo conj
               (assoc post :id (swap! last-id inc))))
    
      (get-posts-for-user
        [this username]
        (get-posts-for-users this [username] :order :timestamp-desc))
      
      (get-posts-for-users
        [_ usernames & {:keys [order]
                        :or {order :timestamp-desc}}]
        (->> @repo
          (filter #(contains? (set usernames) (:username %)))
          (sort-by :timestamp #(compare %2 %1)))))))
      