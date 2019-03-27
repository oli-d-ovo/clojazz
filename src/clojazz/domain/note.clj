(ns clojazz.domain.note
  (:require [overtone.music.pitch :as p]))

(defn note
  [k]
  (try (p/note k)
       (catch Exception e k)))

(defn- resolve-chord
  [root flavour]
  (let [offset (note (str (name root) 3))]
    (->> flavour
         (p/resolve-chord)
         (map (partial + offset))
         set)))

(defn chord
  [ch]
  (println ch)
  (cond
    (coll? ch) (let [[root flavour] ch]
                 (resolve-chord root flavour))
    :else (resolve-chord ch :major)))
