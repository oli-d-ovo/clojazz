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

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(definst triangle-wave [freq 440 attack 0.01 sustain 0.1 release 0.4 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (lf-tri freq)
     vol))

(def res 40320)
(def meter 4)
(def ticks-in-bar (* res meter))

(def ! :rest)
(def - :tie)

(defn- play*
  ([sound-fn metro length tick notes]
   (play* sound-fn metro length tick notes (/ length (count notes))))
  ([sound-fn metro length tick [note :as notes] tpn]
   (let [next-tick (+ tick tpn)]
     (at (metro tick) (case note
                        :rest (stop)
                        :tie identity
                        (sound-fn note)))
     (apply-by (metro next-tick)
               #'play* [sound-fn metro length next-tick (rest notes) tpn]))))

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

(declare play-form)

(defn play-nested-form
  [sound-fn metro tick length form]
  (let [next-tick (+ tick length)]
    (at (metro tick) (play-form sound-fn metro length tick (first form)))
    (apply-by (metro next-tick) #'play-form [sound-fn metro length next-tick (rest form)])))

(defn play-form
  [sound-fn metro length tick form]
  (cond
    (coll? form) (let [length (/ length (count form))]
                   (play-nested-form sound-fn metro tick length form))
    (= :rest form) (stop)
    (= :tie form) form
    :else (sound-fn form)))

(defn play-bars
  [sound-fn tempo bars & {:keys [meter
                                 loop]
                          :or {meter 4
                               loop false}}]
  (let [ticks-in-bar (* res meter)
        length (* ticks-in-bar (count bars))
        metro (metronome (* res tempo))]
    (play-form sound-fn metro length (metro) bars)))
