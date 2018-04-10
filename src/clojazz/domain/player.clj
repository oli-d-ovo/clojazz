(ns clojazz.domain.player
  (:require [clojure.walk :refer [postwalk]]
            [overtone.live :refer [at apply-by stop metronome]]
            [clojazz.domain.note :refer [note]]))

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
    (at (metro tick) (let [next-form (first form)
                           length (if (coll? next-form)
                                    (/ length (count next-form))
                                    length)]
                       (play-form sound-fn metro tick length next-form)))
    (apply-by (metro next-tick) #'play-form [sound-fn metro next-tick length (rest form)])))

(defn play-form
  [sound-fn metro tick length form & keep-length?]
  (cond
    (coll? form) (play-nested-form sound-fn metro tick length form)
    (= :rest form) (stop)
    (= :tie form) form
    :else (sound-fn form)))

(defn play-bars
  [sound-fn tempo bars & {:keys [meter]}]
  (let [ticks-in-bar (* res meter)
        metro (metronome (* res tempo))
        tick (metro)]
    (play-form sound-fn metro tick ticks-in-bar bars)))

(defn- starting-sequence
  [{:keys [sections start-at]}]
  (as-> sections $
       (nth $ (:section start-at))
       (:melody $)
       (drop (dec (:bar start-at)) $)
       (postwalk note $)
       (apply concat $)))

(defn- tune-sequence
  [{:keys [sections play-sequence repeat]}]
  (let [bars (as-> sections $
                   (select-keys $ play-sequence) ;recursify this to allow sequences of sequences
                   (vals $)
                   (map :melody $)
                   (postwalk note $)
                   (apply concat $))]
    (if repeat
      (cycle [bars])
      bars)))

(defn play-tune
  [tune sound-fn]
  (let [tune-tempo (tempo (:tempo tune))
        starting-section (starting-sequence tune)
        main-section (tune-sequence tune)
        bars (cons starting-section main-section)]
    (play-bars sound-fn tune-tempo bars
               :meter (or (:meter tune) 4))))
