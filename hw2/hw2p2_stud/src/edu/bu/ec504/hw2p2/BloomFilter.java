package edu.bu.ec504.hw2p2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Implements a Bloom filter
 */
public class BloomFilter {

    BloomFilter(HashFamily theHashFamily) {
        myHashFamily=theHashFamily;
        filter = new boolean[myHashFamily.getRange()]; // the filter iteself
    }

    void insert(int element) {
        for (int ii=0; ii<myHashFamily.getNumHashes(); ii++)
            filter[ myHashFamily.apply(ii, element) ] = true;
    }

    boolean inFilter(int element) {
        for (int ii = 0; ii < myHashFamily.getNumHashes(); ii++)
            if (!filter[myHashFamily.apply(ii, element)])
                return false;

        return true;
    }

    // fields
    boolean filter[];
    HashFamily myHashFamily;
}
