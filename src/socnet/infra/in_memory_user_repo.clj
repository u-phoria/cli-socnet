(ns socnet.infra.in-memory-user-repo
  (:require [socnet.domain.user :refer [UserRepo]]))

(defn create
  []
  (let [repo (atom {})]
    (reify UserRepo
      
      (create-user-if-absent [_ user]
        (swap! repo (fn [existing]
                      (if (existing (:username user))
                        existing
                        (assoc existing (:username user) user)))))
      
      (lookup-user [_ username]
        (@repo username))

      (update-user [_ user]
        (swap! repo #(assoc % (:username user) user))))))