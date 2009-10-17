(ns ogee
 (:require
   ogee.osgi
   [clojure.contrib.logging :as logging]
   [clojure.contrib.server-socket :as server-socket]))

(def *telnet-server*)

(defn ogee-start [context]
  (dosync (ref-set ogee.osgi/bundle-context context))
  (logging/info "Starting Telnet server on port 9999")
  (try
    (def *telnet-server* (server-socket/create-repl-server 9999))
    (catch Exception e (.printStackTrace e)))
  (logging/info "Ogee started."))

(defn ogee-stop []
  "Shutdown the Ogee runtime."
  (server-socket/close-server *telnet-server*)
  (logging/info "Stopping Ogee..."))


