# clojazz

Lead sheets as code.

## Notation
A single bar is represented as a vector. Each element in that vector is a whole beat, and the elements can themselves be vectors allowing subdivisions within beats. The number of elements in the subvectors determine the length of the subdivisions. For example `[:D :G]` would be eighth notes, `[:D ~ :G]` would be twelfth notes, `[:D ~ :G ~]` would be sixteenths, and so on.

### Examples
A whole note could be written as `[:D]`, `[:D - - -]` , `[[:D] -]`
A half note could be written as `[:D ~]`, `[:D - ~ ~]` , `[[:D] ~]`
A quarter note could be written as `[:D ~ ~ ~]`, `[[:D] ~ ~ ~]`

## Special symbols

### Generic
Tie: `-`
Rest: `~`

### Rhythm section
Hit (for drums): `x`
Root (bass and chords): `r` - for bass will play the root note, for chords will play the chord in root position
Intervals: bass only. `M2`, `m2`, `M3`, `m3`, `d4`, `p4`, `a4`, `d5`, `p5`, `a5`, `m6`, `M6`, `m7`, `M7`, `o`
Voicings/inversions: chords only. Can be a number e.g. `3` for the 3rd inversion, as well as `drop2`, `-rA` (rootless voicing A using 3-5-7-9), `-rB` (rootless voicing B using 7-9-3-5). Could also be a vector, e.g. `[3 5 7 9]` for the rootless voicing A.
