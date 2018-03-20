(ns user)

(use 'overtone.live)
(use 'overtone.inst.piano)

(defn rand-chords
  []
  (doseq [[ch notes] CHORD]
    (let [offset (rand-int 12)]
      (println (REVERSE-NOTES offset) ch)
      (doseq [note notes]
        (piano (+ note 60 offset)))
      (Thread/sleep 1000))))

(defn rand-251
  []
  (let [chords [[2 :m7] [7 #{-5 -2 0 4}] [0 :M7]]
        offset (rand-int 12)]
    (doseq [[root flav] chords]
      (doseq [note (or (flav CHORD) flav)]
        (piano (+ note 60 offset root)))
      (Thread/sleep 1000))))

(defn recursive-251
  [tonal-centre next-fn amount]
  (let [chords [[2 :m7] [7 #{-5 -2 0 4}] [0 :M7]]]
    (doseq [[root flav] chords]
      (doseq [note (or (flav CHORD) flav)]
        (piano (+ note root tonal-centre)))
      (Thread/sleep 1000))
    (recur (next-fn tonal-centre amount) next-fn amount)))

(def res 40320)
(def meter 4)
(def ticks-in-bar (* res meter))

(defn play
  ([notes sound-fn tempo]
   (let [metro (metronome (* res tempo))
         notes (into (clojure.lang.PersistentQueue/EMPTY) notes)]
     (play notes sound-fn metro 0)))
  ([notes sound-fn metro tick]
   (let [subdivisions (count notes)
         next-note (peek notes)
         notes (conj (pop notes) next-note)
         tpb (/ ticks-in-bar subdivisions)
         next-tick (+ tpb tick)]
     (at (metro tick) (sound-fn next-note))
     (apply-by (metro next-tick) play notes sound-fn metro next-tick []))))
