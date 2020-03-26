package edu.bu.ec504.hw2.mapReduceExamples.commonCharacters;

import edu.bu.ec504.hw2.EmissionTemplate.CompareEmission;
import edu.bu.ec504.hw2.Reducer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Reducer for a map-reduce pair the calculates the number of items each character occurs in a text.
 * The reducer receives characters, one at a time, and various counts of their repetitions in the text; it adds
 * up the counts and emits the character together with the total number of its repetitions in the text.
 * The print method sorts all received (character,# of repetitions) pairs by the number of repetitions.
 */

public class CommonCharactersReducer extends Reducer {

  public CommonCharactersReducer(String theKey, ArrayList<Integer> theValue) {
    super(theKey, theValue);
  }

  public CommonCharactersReducer() { super(); }

  @Override
  public ReducerEmissionList call() {
    // CHAR-COUNT EXAMPLE: add up all the emissions in the <value> field
    int sum = 0;
    for (Integer ii : value) {
      sum += ii;
    }
    ReducerEmissionList EL= new ReducerEmissionList();
    EL.add(new ReducerEmission(key, sum));
    return EL;
  }

   public String print() {
    // CHAR-COUNT EXAMPLE:  return a string of all reduce emissions in sorted order

       // ... collect ReducerEmissions
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
    results.sort(new CompareEmission());

    // ... output in sorted order
    StringBuilder output = new StringBuilder();
    for (ReducerEmission result : results)
      output.append(result.getKey()) // print out each emission from the reducer, in turn
              .append(": ")
              .append(result.getValue())
              .append("\n");
    return output.toString();
  }
}
