(ns clojazz.domain.player
  (:require [overtone.live :refer [at apply-by stop metronome]]))

(def res 40320)

(def tempo
  (let [i (rand-int 30)]
    {:slow (+ i 105)
     :medium (+ i 135)
     :fast (+ i 165)}))

(declare play-form)

(defn play-nested-form
  [sound-fn metro tick length form]
  (let [next-tick (+ tick length)]
    (at (metro tick) (play-form sound-fn metro tick length (first form)))
    (apply-by (metro next-tick) #'play-form [sound-fn metro next-tick length (rest form)])))

(defn play-form
  [sound-fn metro tick length form]
  (cond
    (coll? form) (let [length (/ length (count form))]
                   (play-nested-form sound-fn metro tick length form))
    (= :rest form) (stop)
    (= :tie form) form
    :else (sound-fn form)))

(defn play-bars
  [sound-fn tempo bars & {:keys [meter loop]
                          :or {meter 4 loop false}}]
  (let [ticks-in-bar (* res meter)
        length (* ticks-in-bar (count bars))
        metro (metronome (* res tempo))
        tick (metro)]
    (play-form sound-fn metro tick length bars)))
