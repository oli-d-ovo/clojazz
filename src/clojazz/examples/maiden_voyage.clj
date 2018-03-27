(ns clojazz.examples.maiden-voyage
  (:require [clojazz.domain.tune :refer [deftune]]))

(def v [4 7 9 11])

(deftune maiden-voyage
  :tempo :slow
  :groove :swing

  :sections
  [{:melody  [                              [- - - [:A3 :D4]]]}

   {:melody  [[:D4                ] [-] [-] [! - - [:C4 :D4]]
              [[:Eb4 :F4] :C4  - -] [-] [-] [! - - [:A3 :D4]]]
    :harmony [[[:D :7sus4]        ] [-] [-] [-]
              [[:F :7sus4]        ] [-] [-] [-]]}

   {:melody  [[:D4                ] [-] [-] [! - - [:C4 :D4]]
              [[:Eb4 :F4] :C4  - -] [-] [-] [! - - [:C4 :F4]]]
    :harmony [[[:D :7sus4]        ] [-] [-] [-]
              [[:F :7sus4]        ] [-] [-] [-]]}

   {:melody  [[:F4                ] [-] [-] [! - - [:Eb4 :E4]]
              [[:E4 :Gb4] :Db4 - -] [-] [-] [! - - [:A3  :D4]]]
    :harmony [[[:Eb :7sus4]       ] [-] [-] [-]
              [[:Db :7sus4]       ] [-] [-] [-]]}]
  :start-at {:section 0 :bar 1}
  :play-sequence [1 2 3]
  :repeat false

  :rhythm
  {:ride   [[x     x [x x] -] [[x x] [x x] -      -     ]]
   :bass   [[[r !] r [- r] -] [-     [- r] [- p5] [o p5]]]
   :chords [[[v !] v [- v] -] [-     [- v] -      -     ]]})
