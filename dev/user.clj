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
