package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Mapper;

public class mapper4c extends Mapper {
    public int line;
    /**
     * A helpful constructor for instantiating a mapper based on a key-value pair.
     */
    public mapper4c(Integer theKey, String theValue) { super(theKey, theValue); }

    /**
     * Default constructor - required by the mapReduce engine, though it need not do anything.
     */
    public mapper4c() { super(); }

    /**
     * 	This does the actual work of your mapper.
     */
    @Override
    public MapperEmissionList call() {
        MapperEmissionList EmissionList = new MapperEmissionList();
        int start = 0;
        int end = 0;
        for (int ii=0; ii<value.length(); ii++) {
            if (Character.isLetter(value.charAt(ii))) {
                end ++;
                if (ii == value.length() - 1 && start != end) {
                    EmissionList.add(
                            new MapperEmission(Integer.toString(end - start), 1)
                    );
                }
            } else {
                if (start != end) {
                    EmissionList.add(
                            new MapperEmission(Integer.toString(end - start), 1)
                    );
                }
                end += 1;
                start = end;
            }
        }

        return EmissionList;
    }
}
