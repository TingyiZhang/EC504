package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Reducer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class reducer4b extends Reducer {

    public reducer4b(String theKey, ArrayList<Integer> theValue) {
        super(theKey, theValue);
    }

    /**
     * 	Default constructor - required by the mapReduce engine, though it need not do anything.
     */
    public reducer4b() { super(); }

    /**
     * 	This does the actual work of your reducer.
     */
    @Override
    public ReducerEmissionList call() {
        int sum = 0;
        for (Integer ii : value) {
            if (ii == -1)
                return new ReducerEmissionList();
            sum += ii;
        }
        ReducerEmissionList EL= new ReducerEmissionList();
        EL.add(new ReducerEmission(key, sum));
        return EL;
    }

    /**
     * Coalesces reduction output.
     * @return A string representing the coalesced reduction output.
     */
    @Override
    public String print() {
        ArrayList<ReducerEmission> results = new ArrayList<>();
        for (Future<ReducerEmissionList> future: Reducer.getFutures()) {
            try {
                List<ReducerEmission> emittedList = future.get();
                results.addAll(emittedList);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // ... sort by key
        results.sort(new reducer4b.CompareEmissionKey());

        // ... output in sorted order
        StringBuilder output = new StringBuilder();
        for (ReducerEmission result : results)
            output.append(result.getKey()) // print out each emission from the reducer, in turn
                    .append("\n");
        return output.toString();
    }

    // Comparator
    public static class CompareEmissionKey implements Comparator<EmissionTemplate> { // compare by emissions by key field
        @Override
        public int compare(EmissionTemplate o1, EmissionTemplate o2) {
            return -o1.getKey().compareTo(o2.getKey());
        }
    }
}
