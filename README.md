# EC504
This is a repo for homeworks of EC504.

 - [Homework0](https://github.com/TingyiZhang/EC504#homework0)
 - [Homework1](https://github.com/TingyiZhang/EC504#homework1)
 - [Homework2](https://github.com/TingyiZhang/EC504#homework2)

## Homework0
This is homework0 for EC504 Advanced Data Structure.

### SameGameTris
The hw0p3 file contains the code of a game called SameGameTris written by Prof. Ari Trachtenberg. This game is basically the combination of Same Game and Tetris.

<img src = "https://github.com/TingyiZhang/EC504-Homework0/blob/master/SameGameTris.gif">

What I needed to do, is modified the smarterBrain.java file, and create an automatic player.

#### My strategy
Ranked by priority:
1. Click on the longest same-color line in the highest column.
2. Click on the circle that can score the most in the highest column.
3. If every circle in the highest column only scores 1 point, then find the circle on board that can score the most.

#### Defect
I think my logic is good, but it still performes under my expectation.
My thoughts:
- Time complexity, there are so many for loops in my code. But starngely, it didn't perform well on 10 by 10 board, which doesn't make sense.
- Maybe there are better ways than mine, but I still think my logic is correct.
- Luck: because new circles are random, so being lucky is important...

### Closest Distance
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

#### My solution
1. Find the longest common subsequence using dynamic programming.
2. My way to calculate distance: |w'| - |LCS| + gap.
- Gap:
For example the LCS is "abcd":
-1-a-2-b-2-c-1-d-1-
1 is the gap if it exists.

## Homework1
### Distinct Counter
Create an counter to predict the number of distinct strings. The output is a Byte array with limited size. And the tester will create a new class to copy the state, whiche means everything within the origin class is cleared(only store the result in state).

#### My solution
I use Flajolet Martin algorithm to guess the number, with 10 Hash functions.

The Hash function is: ``` m + n * s.hashCode()```.
.hashCode() is a function in Java to hash the string.

### LZW compressor
In this problem, you will be losslessly compressing text in a scheme reminiscent of Lempel-Ziv compression. You will build a class that efficiently compresses plaintext into a list, which can be serialized and transmitted to a receiver. The receiver will then be able to uncompress this list to reproduce the plaintext.

The main idea behind our efficient storage is to maintain a dictionary of common substrings, and refer to these substrings rather than reproduce them. As such, we encode a string into a sequence of Atom s, with each Atom pointing to an index in our dictionary and a string to follow it.

#### My solution
1. For every line, find every common prefix and store them into the dictionary.
2. And went through the text line by line and find the longest common prefix in the dictionary and store the result in an atom.

## Homework2
### Hash Family
The goal of this problem is to build to hash family that every hash function is independent to each other.
- My solution: Universal Hashing

### BST
This problem ask us to do some operations on a bst. I'm running out of time so I can't finish this well...

### MapReduce
Using MapReduce to solve some specific tasks:
- Counting the number of unique words
- List words that appear only on even numbered lines.
- Produce a histogram of the lengths of words in the file
- Produce a list of pairs of lines with the number of words they have in common.
