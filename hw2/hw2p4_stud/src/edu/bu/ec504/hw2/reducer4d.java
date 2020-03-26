package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Reducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class reducer4d extends Reducer {

    public reducer4d(String theKey, ArrayList<Integer> theValue) {
        super(theKey, theValue);
    }

    /**
     * 	Default constructor - required by the mapReduce engine, though it need not do anything.
     */
    public reducer4d() { super(); }


    /**
     * 	This does the actual work of your reducer.
     */
    @Override
    public ReducerEmissionList call() {
        ReducerEmissionList EL= new ReducerEmissionList();
        EL.add(new ReducerEmission(key, value.get(0)));
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

        // ... sort by value
        results.sort(new EmissionTemplate.CompareEmission());

        // ... output in sorted order
        StringBuilder output = new StringBuilder();
        for (ReducerEmission result : results)
            output.append(result.getKey()) // print out each emission from the reducer, in turn
                    .append(": ")
                    .append(result.getValue() + " words in common")
                    .append("\n");
        return output.toString();
    }

}
