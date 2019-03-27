(ns clojazz.domain.player
  (:require [clojure.walk :refer [postwalk]]
            [overtone.live :refer [at apply-by stop metronome]]
            [clojazz.domain.note :refer [note chord]]))

(def res 40320)

(def tempo
  (let [i (rand-int 30)]
    {:slow (+ i 105)
     :medium (+ i 135)
     :fast (+ i 165)}))

(defn- play-chord
  [sound-fn notes]
  (doseq [note notes]
    (sound-fn note)))

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
    (set? form) (play-chord sound-fn form)
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

(defn- melody->notes
  [melody]
  (postwalk note melody))

(defn- harmony->notes
  [harmony]
  (println harmony)
  (map chord harmony))

(defn- section->notes
  [{:keys [melody harmony]}]
  (concat (melody->notes melody)
          (harmony->notes harmony)))

(defn- starting-sequence
  [{:keys [sections start-at]}]
  (as-> sections $
    (nth $ (:section start-at))
    (section->notes $)))

(defn- get-sequence
  [{:keys [sections play-sequence]}]
  (-> sections
      (select-keys play-sequence);recursify this to allow sequences of sequences
      vals))

(defn- tune-sequence
  [{:keys [repeat] :as sections}]
  (let [bars (as-> sections $
                   (get-sequence $)
                   (map section->notes $))]
    (if repeat
      (cycle [bars])
      bars)))

(defn play-tune
  [tune sound-fn]
  (let [tune-tempo (tempo (:tempo tune))
        starting-section (starting-sequence tune)
        main-section (tune-sequence tune)
        sections (cons starting-section main-section)]
    (println sections)
    (doseq [section sections]
      (play-bars sound-fn tune-tempo section
                 :meter (or (:meter tune) 4)))))
