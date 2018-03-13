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

(def kick (sample (freesound-path 2086)))

(def res 2)
(def metro (metronome (* res 100)))

(defn play
  [tempo notes sound-fn]
  (let [notes (cycle notes)
        subdivisions (count notes)
        beat (metro)
        next-beat (metro (+ (dec res) beat))]
    (at next-beat (sound-fn (first notes)))
    (apply-by (metro (+ (dec res) beat)) play tempo (rest notes) sound-fn [])))
