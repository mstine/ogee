(ns ogee
  (:require 
     ogee.osgi
     [clojure.contrib.logging :as logging]
     [clojure.contrib.server-socket :as server-socket]))

(def telnet-server (ref nil))

(defn ogee-start [context]
  (dosync (ref-set ogee.osgi/bundle-context context))
  (logging/info "Starting Telnet server on port 9999")
  (dosync (ref-set telnet-server (server-socket/create-repl-server 9999)))
  (logging/info "Ogee started."))

(defn ogee-stop []
  "Shutdown the Ogee runtime."
  (logging/info "Stopping Ogee...")
  (server-socket/close-server @telnet-server))





