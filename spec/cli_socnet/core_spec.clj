(ns cli-socnet.core-spec
  (:require [speclj.core :refer :all]
            [clj-time.core :as t]
            [cli-socnet.core :refer :all]))

(def ts [(-> 3 t/days t/ago) (-> 2 t/hours t/ago) (-> 30 t/minutes t/ago) (-> 5 t/minutes t/ago) (-> 20 t/seconds t/ago)])
(defn fake-now []
  (let [a (atom ts)]
    #(let [res (first @a)]
       (swap! a rest)
       res)))

(describe "reading a user's posted messages"
          
  (around [it]
    (with-redefs [t/now (fake-now)]
      (it)))
  
  (it "reads post by a first-time user"
      (-> (create)
        (post  "joe" "hi")
        
        (read-posts "joe")
        (should= [(->Post "joe" "hi" (first ts))])))

  (it "reads multiple interleaved posts, ordering them by most recent first"
      (-> (create)
        (post "joe" "hi")
        (post "schmoe" "hello")
        (post "doe" "how's it going??")
        (post "joe" "great")
        (post "joe" "just great")
        
        (read-posts "joe")
        (should= [(->Post "joe" "just great" (-> 4 ts))
                  (->Post "joe" "great" (-> 3 ts))
                  (->Post "joe" "hi" (first ts))]))))

(describe "viewing aggregated posts on a wall"
          
  (around [it]
      (with-redefs [t/now (fake-now)]
        (it)))
  
  (it "shows only the user's own posts when they aren't following anyone"
      (-> (create)
        (post "joe" "hi")
        
        (wall "joe")
        (should= [(->Post "joe" "hi" (first ts))])))
  
  (it "shows own and followed users' posts, ordered newest first"
      (-> (create)
        (post "joe" "hi")
        (post "schmoe" "hello joe")
        (follow "joe" "schmoe")
        (post "roe" "hi guys")
        (post "schmoe" "hi roe")
        
        (wall "joe")
        (should= [(->Post "schmoe" "hi roe" (-> 3 ts))
                  (->Post "schmoe" "hello joe" (second ts))
                  (->Post "joe" "hi" (first ts))])))
  
  (it "supports following multiple users"
      (-> (create)
        (follow "joe" "roe")
        (follow "joe" "doe")
        (follow "joe" "schmoe")
        (post "joe" "hi")
        (post "schmoe" "hi from schmoe")
        (post "roe" "hi from roe")
        (post "doe" "hi from doe")
        
        (wall "joe")
        (should= [(->Post "doe" "hi from doe" (-> 3 ts))
                  (->Post "roe" "hi from roe" (-> 2 ts))
                  (->Post "schmoe" "hi from schmoe" (second ts))
                  (->Post "joe" "hi" (first ts))]))))
  
(run-specs)
