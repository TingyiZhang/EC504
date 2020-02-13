# EC504-Homework0
This is homework0 for EC504 Advanced Data Structure.

## SameGameTris
The hw0p3 file contains the code of a game called SameGameTris written by Prof. Ari Trachtenberg. This game is basically the combination of Same Game and Tetris.

<img src = "https://github.com/TingyiZhang/EC504-Homework0/blob/master/SameGameTris.gif">

What I needed to do, is modified the smarterBrain.java file, and create a automatic player.

### My strategy
Ranked by priority:
1. Click on the longest same-color line in the highest column.
2. Click on the circle that can score the most in the highest column.
3. If every circle in the highest column only scores 1 point, then find the circle on board that can score the most.

## Closest Distance
The code is in hw0p4 myFindSimilar.java.

A word w is a String of arbitrary length. Similarly, the dictionary D contains |D| String s of arbitrarily length.
The distance ẟ(w,w') between w and a word w' ∈ D is defined as the number of changes needed to make to w to get w', where there are two acceptable changes:
- insert(c, i) - inserts character c after the i -th character of w;
- bulkRemove(i,j) - removes all characters in w from the i th character (inclusive) to the j th character (exclusive).
In the event of a tie, the funcion in myFindSmilar.java should return the first one that appears in the dictionary.

For example:
```
oast
oath
ogre
onyx
oss
```
- ẟ(ores, oast) = 3 ... remove re, insert a, insert t
- ẟ(ores, oath) = 4 ... remove res, insert a, insert t, insert h
- ẟ(ores, ogre) = 2 ... insert g and remove s.
- ẟ(ores, onyx) = 4 ... remove res, insert n, insert y, insert x
- ẟ(ores, oss) = 2 ... remove re, insert s

### My solution
1. Find the longest common subsequence using dynamic programming.
2. My way to calculate distance: |w'| - |LCS| + gap.
- Gap:
For example the LCS is "abcd":
-1-a-2-b-2-c-1-d-1-
1 is the gap if it exists.
