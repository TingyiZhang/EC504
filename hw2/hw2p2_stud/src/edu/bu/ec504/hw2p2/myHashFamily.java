package edu.bu.ec504.hw2p2;

import java.lang.reflect.Array;
import java.util.*;

public class myHashFamily extends HashFamily {
    public ArrayList<Integer> m = new ArrayList<>();
    public ArrayList<Integer> n = new ArrayList<>();
    public Integer p;

    /**
     * @inheritDoc
     */
    public myHashFamily(int numHashes, int range) {
        super(numHashes, range);
        Random rand = new Random();
        p = getRange() * 2;
        while(!isPrime(p)) {
            p += 1;
        }
        for (int i = 0; i < this.getNumHashes(); i++) {
            m.add(rand.nextInt(p));
            n.add(rand.nextInt(p));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int apply(int index, int elem) {
        return ((m.get(index) + n.get(index) * elem) % p) % getRange();
    }

    public boolean isPrime(int num) {
        int sqrt = (int)Math.sqrt(num) + 1;
        for (int i = 2; i < sqrt; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

}
