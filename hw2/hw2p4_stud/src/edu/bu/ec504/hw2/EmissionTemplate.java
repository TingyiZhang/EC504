package edu.bu.ec504.hw2;

import java.util.Comparator;

/**
 * An immutable class that stores one emission containing a (String,integer) pair.
 */
public class EmissionTemplate {
  // CONSTRUCTOR
  EmissionTemplate(String theKey, Integer theValue) {
    emissionKey =theKey;
    emissionValue=theValue;
  }

  // NESTED CLASS
  /**
   * A class for comparing {@link EmissionTemplate}s by value.
   */
  public static class CompareEmission implements Comparator<EmissionTemplate> { // compare by emissions by value field
    @Override
    public int compare(
        EmissionTemplate o1,
        EmissionTemplate o2
    ) {
      return -o1.emissionValue.compareTo(o2.emissionValue);
    }
  }

  // GETTERS
  public String getKey() {
    return emissionKey;
  }
  public Integer getValue() {
    return emissionValue;
  }

  // FIELDS
  private final String emissionKey;
  private final Integer emissionValue;
}
