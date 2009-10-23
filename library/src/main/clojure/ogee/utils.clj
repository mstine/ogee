(ns ogee.utils
 (:require 
   [clojure.contrib.str-utils :as str-utils]
   [clojure.contrib.logging :as logging]))

(defn exception-add-text
  "Wrap exception e in a RuntimeException and add texts separated by ' ' as message."
  [e & texts]
  (RuntimeException. (str-utils/str-join " " texts) e))
