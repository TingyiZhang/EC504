// Copyright 2020 Tingyi Zhang tingyi97@bu.edu
package edu.bu.ec504.hw0p4;

import javax.security.auth.callback.CallbackHandler;
import java.util.*;

public class myFindSimilar implements FindSimilar {

    public Map<String, Integer> dict = new HashMap<>();
    public int index = 0;
    /**
     * {@inheritDoc}
     */
    @Override
    public int addToDictionary(String entry) {
        dict.put(entry, index);
        return index++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex(String entry) {
        return dict.get(entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int closestWord(String word) {
        int closest = 100000;
        int smallestIndex = index;
        LinkedList<String> entry = new LinkedList<>();
        String similar = new String();
        for (String s : this.dict.keySet()) {
            int distance = calculateDistance(word, s);
            if (distance <= closest) {
                closest = distance;
                entry.add(s);
            }
        }

        // find the word that has the smallest index
        for (int i = 0; i < entry.size(); i++) {
            if (calculateDistance(word, entry.get(i)) == closest) {
                if (getIndex(entry.get(i)) < smallestIndex) {
                    smallestIndex = getIndex(entry.get(i));
                    similar = entry.get(i);
                }
            }
        }

        return getIndex(similar);
    }

    public int calculateDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        LinkedList<Character> lcs = new LinkedList<>();

        // calculate the length of lcs of s1 and s2
        for (int i = 0; i < s1.length(); ++i)
            for (int j = 0; j < s2.length(); ++j)
                if (s1.charAt(i) == s2.charAt(j)) {
                    dp[i + 1][j + 1] = 1 + dp[i][j];
                }
                else dp[i + 1][j + 1] =  Math.max(dp[i][j + 1], dp[i + 1][j]);

        // find the subsequence and store their index in a LinkedList
        int l1 = s1.length(); int l2 = s2.length();
        LinkedList<Integer> index = new LinkedList<>(); // index of subsequence in word (using to calculate remove steps)
        while (l1 > 0 && l2 > 0) {
            if (s1.charAt(l1 - 1) == s2.charAt(l2 - 1)) {
                lcs.add(s1.charAt(l1 - 1));
                index.add(l1 - 1); // index is in reverse order of the actual index
                l1--; l2--;
            } else {
                if (dp[l1 - 1][l2] > dp[l1][l2 - 1]) {
                    l1--;
                } else {
                    l2--;
                }
            }
        }

        // calculate remove steps
        int steps = 0;
        if (dp[s1.length()][s2.length()] > 0) {
            if (s1.length() - 1 - index.getFirst() > 0) steps++; // check if there are letters after the last letter of lcs
            if (index.getLast() > 0) steps++; // check if there are letters before the first letter of lcs
            for (int i = 0; i < index.size() - 1; i++) {
                if (index.get(i) - index.get(i + 1) > 1) steps++; // check if the lcs is consecutive
            }
        } else {
            steps++; // if there is no lcs, it takes at least one step to remove all the letters
        }

        if (index.size() == s1.length()) { // if lsc == word, no need to insert anything
            return steps; // steps of bulk remove
        } else {
            return steps + s2.length() - dp[s1.length()][s2.length()]; // remove steps + insertion steps
        }
    }
}

