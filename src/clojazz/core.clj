(ns clojazz.core
  (:require [overtone.core :as overtone]
            [clojazz.domain.player :refer [play-tune]])
  (:gen-class))


(defn find-tune
  [tune-name]
  (->> (str "clojazz.examples." tune-name "/" tune-name)
       symbol
       find-var
       var-get))

(defn -main
  "I don't do a whole lot ... yet."
  [& [tune-name]]
  (overtone/boot-server)
  (play-tune (find-tune tune-name)))
