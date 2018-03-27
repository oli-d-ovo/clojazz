(ns clojazz.domain.note
  (:require [overtone.music.pitch :as p]))

(defn note
  [k]
  (try (p/note k)
       (catch Exception e k)))
