(ns clojazz.domain.tune
  (:require [clojure.walk :refer [postwalk-replace]]))

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
  (partial postwalk-replace smap))

(defmacro deftune
  [tune-name & {tune-voicings :voicings :as spec}]
  (let [voicings# (merge voicings tune-voicings)]
    `(def ~tune-name
       ~(-> spec
            (dissoc :voicings)
            (update-in [:sections] (replace-symbols (select-keys notation ['! '-])))
            (update-in [:rhythm] (replace-symbols notation))
            (update-in [:rhythm :bass] (replace-symbols intervals))
            (update-in [:rhythm :chords] (replace-symbols voicings#))))))
