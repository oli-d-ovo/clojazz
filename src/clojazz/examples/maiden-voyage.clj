(ns clojazz.examples.maiden-voyage
  (:require [clojazz.domain.notation :refer :all]
            [clojazz.domain.intervals :refer :all]))

(def v [4 7 9 11])

(def maiden-voyage
  {:tempo :medium
   :swing true

   :sections
   [{:melody [[- - - [:A :D]]]}

    {:melody [[:D             ] [-] [-] [! - - [:C :D]]
              [[:Eb :F] :D - -] [-] [-] [! - - [:A :D]]]
     :chords [[[:D :7sus4]    ] [-] [-] [-]
              [[:F :7sus4]    ] [-] [-] [-]]}

    {:melody [[:D             ] [-] [-] [! - - [:C :D]]
              [[:Eb :F] :D - -] [-] [-] [! - - [:C :F]]]
     :chords [[[:D :7sus4]    ] [-] [-] [-]
              [[:F :7sus4]    ] [-] [-] [-]]}

    {:melody [[:F           ] [-] [-] [! - - [:Eb :E]]
              [[:E :Gb] :Db ] [-] [-] [! - - [:A  :D]]]
     :chords [[[:Eb :7sus4] ] [-] [-] [-]
              [[:Db :7sus4] ] [-] [-] [-]]}]
   :start-at {:section 0
              :bar [1 4]}
   :sequence [1 2 3]

   :rhythm
   {:ride   [[x     x [x x] -] [[x x] [x x] -      -     ]]
    :bass   [[[r !] r [- r] -] [-     [- r] [- p5] [o p5]]]
    :chords [[[v !] v [- v] -] [-     [- v] -      -     ]]}})
