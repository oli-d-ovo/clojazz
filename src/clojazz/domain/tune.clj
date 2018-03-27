(ns clojazz.domain.tune
  (:require [clojure.walk :refer [postwalk-replace]]
            [clojure.set :refer [difference]]))

(def ^:private notation
  {'x :hit
   '- :tie
   '! :rest})

(def ^:private intervals
  {'r  0
   'p5 7
   'o  12})

(def ^:private voicings
  {'d2 [5 1 3 7]})

(defn ^:private clean-symbols
  [smap]
  (let [interns (->> (ns-interns *ns*) keys)
        clean-smap (apply dissoc smap interns)
        cleaned-symbols (difference (set (keys smap))
                                    (set (keys clean-smap)))]
    (when (not-empty cleaned-symbols)
      (println "WARNING: You have used reserved symbols in your namespace. This may cause unwanted behaviour.")
      (println "Using your definitions of:" cleaned-symbols))
    clean-smap))

(defn ^:private replace-symbols
  [smap]
  (partial postwalk-replace (clean-symbols smap)))

(defmacro deftune
  [tune-name & spec]
  (let [spec (apply hash-map spec)]
    `(def ~tune-name
       ~(-> spec
            (update :sections (replace-symbols (clean-symbols (select-keys notation ['! '-]))))
            (update :rhythm (replace-symbols notation))
            (update-in [:rhythm :bass] (replace-symbols intervals))
            (update-in [:rhythm :chords] (replace-symbols voicings))))))
