(ns socnet.infra.in-memory-post-repo-spec
  (:require [speclj.core :refer :all]
            [clj-time.core :as t]
            [socnet.domain.post :as p]
            [socnet.infra.in-memory-post-repo :refer :all]))

(def dummy-post (with-redefs [t/now (constantly (-> 30 t/seconds t/ago))]
                  (p/create-post "dummy" "hello")))
(def dummy-post2 (p/create-post "dummy" "hola"))

(def ts [(-> 3 t/days t/ago) (-> 2 t/hours t/ago) (-> 30 t/minutes t/ago) (-> 5 t/minutes t/ago) (-> 20 t/seconds t/ago)])
(defn fake-now []
  (let [a (atom ts)]
    #(let [res (first @a)]
       (swap! a rest)
       res)))

(describe "in memory store for posts"
          (around [it]
                  (with-redefs [t/now (fake-now)]
                    (it)))
          
          (it "allows creation and retrieval of a new post"
              (-> (create)
                (doto (p/add dummy-post))
                (p/get-posts-for-user "dummy")

                (should= [(assoc dummy-post :id 1)])))
          
          (it "gives new posts an id"
              (-> (create)
                (doto (p/add dummy-post))
                (p/get-posts-for-user "dummy")
                first
                :id
                should-not-be-nil))
          
          (it "retrieves posts for a single user in reverse timestamp order"
              (-> (create)
                (doto
                  (p/add dummy-post)
                  (p/add dummy-post2))
                (p/get-posts-for-user "dummy")
                
                (should= [(assoc dummy-post2 :id 2)
                          (assoc dummy-post :id 1)])))
          
          (it "retrieves posts for a set of users in reverse timestamp order"
              (let [repo (create)
                    posts (mapv p/create-post ["fred" "jed" "ted" "fred"]
                                              ["one" "two" "three" "four"])
                    posts (mapv #(assoc %1 :id %2) posts [1 2 3 4])]
                (mapv (partial p/add repo) posts)
                
                (should= (mapv posts [3 2 0])
                         (p/get-posts-for-users repo ["fred" "ted"] :order :timestamp-desc)))))

(run-specs)