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
  {'drop2 [5 1 3 7]})

(defn ^:private replace-symbols
  [smap]
  (let [interns (->> (ns-interns *ns*) keys)
        clean-smap (apply dissoc smap interns)
        cleaned-symbols (difference (set (keys smap))
                                    (set (keys clean-smap)))]
    (when (not-empty cleaned-symbols)
      (println "WARNING: You have used reserved symbols in your namespace. This may cause unwanted behaviour.")
      (println "Using your definitions of:" cleaned-symbols))
    (partial postwalk-replace clean-smap)))

(defmacro deftune
  [tune-name & spec]
  `(def ~tune-name
     ~(-> spec
          (update-in [:sections] (replace-symbols (select-keys notation ['! '-])))
          (update-in [:rhythm] (replace-symbols notation))
          (update-in [:rhythm :bass] (replace-symbols intervals))
          (update-in [:rhythm :chords] (replace-symbols voicings)))))
