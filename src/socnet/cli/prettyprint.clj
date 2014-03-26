(ns socnet.cli.prettyprint
  (:require [clj-time.coerce :as tc])
  (:import (com.ocpsoft.pretty.time PrettyTime)
           (java.util Date)))

(defn print-post
  ([post] (print-post post true))
  ([post with-username?]
    (println (str (when with-username?
                    (format "%s - " (:username post)))
                  (:message post)
                  " ("
                  (.format (PrettyTime. (tc/to-date (:timestamp post))) (Date.))
                  ")"))))