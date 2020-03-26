package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Mapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class mapper4d extends Mapper {
    /**
     * A helpful constructor for instantiating a mapper based on a key-value pair.
     */
    public mapper4d(Integer theKey, String theValue) { super(theKey, theValue); }

    /**
     * Default constructor - required by the mapReduce engine, though it need not do anything.
     */
    public mapper4d() { super(); }

    /**
     * 	This does the actual work of your mapper.
     */
    @Override
    public MapperEmissionList call() {
        List<String> thisLine = wordList(this.value);
        MapperEmissionList EmissionList = new MapperEmissionList();
        for (Mapper m : tasks) {
            if (m.key > this.key) {
                List<String> pairLine = wordList(m.value);
                pairLine.retainAll(thisLine);
                int numCommon = pairLine.size();
                EmissionList.add(
                        new MapperEmission("line " + this.key.toString() + " and line " + m.key.toString(), numCommon)
                );
            }
        }
        return EmissionList;
    }

    public List<String> wordList(String line) {
        int start = 0;
        int end = 0;
        List<String> res = new ArrayList<>();
        for (int ii = 0; ii < line.length(); ii++) {
            if (Character.isLetter(line.charAt(ii))) {
                end++;
                if (ii == line.length() - 1 && start != end) {
                    res.add(line.substring(start, end));
                }
            } else {
                if (start != end) {
                    res.add(line.substring(start, end));
                }
                end += 1;
                start = end;
            }
        }
        return res;
    }
}
