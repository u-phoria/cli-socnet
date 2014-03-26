(ns socnet.infra.in-memory-user-repo-spec
  (:require [speclj.core :refer :all]
            [socnet.domain.user :as u]
            [socnet.infra.in-memory-user-repo :refer :all]))

(def dummy-user (u/create "dummy"))

(describe "in memory store for users"
          (it "should support adding a user"
              (-> (create)
                (doto
                  (u/create-user-if-absent (u/create "dummy")))
                (u/lookup-user "dummy")
                (should= dummy-user)))
          
          (it "should support updating a user"
              (let [repo (create)]
                (u/create-user-if-absent repo (u/create "dummy"))
                
                (u/update-user repo (->
                                      (u/lookup-user repo "dummy")
                                      (u/follow "moo")))
                
                (should= (u/follow dummy-user "moo")
                         (u/lookup-user repo "dummy")))))

(run-specs)