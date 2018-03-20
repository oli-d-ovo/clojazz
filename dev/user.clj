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

(defn- wrap-in
  [empty-coll x]
  (if (= (type x) (type empty-coll))
    x
    (conj empty-coll x)))

(def res 40320)
(def meter 4)
(def ticks-in-bar (* res meter))

(defn- play*
  ([sound-fn metro length tick notes]
   (play* sound-fn metro length tick notes (/ length (count notes))))
  ([sound-fn metro length tick [note :as notes] tpn]
   (if note
     (let [next-tick (+ tick tpn)]
       (at (metro tick) (sound-fn note))
       (apply-by (metro next-tick)
                 #'play* [sound-fn metro length next-tick (rest notes) tpn])))))

(defn- looper
  [sound-fn metro length tick notes]
  (let [subdivisions (count notes)
        tpn (/ length subdivisions)
        next-tick (+ tick tpn)
        next-note (peek notes)
        notes (conj (pop notes) next-note)]
    (at (metro tick) (play* sound-fn metro tpn tick next-note))
    (apply-by (metro next-tick)
              #'looper [sound-fn metro length next-tick notes])))

(defn play
  ([notes sound-fn tempo]
   (let [metro (metronome (* res tempo))
         notes (->> notes
                    (map (partial wrap-in []))
                    (into (clojure.lang.PersistentQueue/EMPTY)))]
     (play notes sound-fn metro 0)))
  ([notes sound-fn metro tick]
   (looper sound-fn metro ticks-in-bar tick notes)))
