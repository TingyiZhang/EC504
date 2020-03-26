package edu.bu.ec504.hw2.mapReduceExamples.commonCharacters;

import edu.bu.ec504.hw2.Mapper;

/**
 * Mapper for a map-reduce pair the calculates the number of items each character occurs in a text.
 * The mapper emits each character, one at a time, adjoined to the number 1.
 */
public class CommonCharactersMapper extends Mapper {

  public CommonCharactersMapper(Integer theKey, String theValue) {
    super(theKey, theValue);
  }

  public CommonCharactersMapper() { super(); }

  @Override
  public MapperEmissionList call() {
    /* THIS IS WHERE YOUR MAP FUNCTION GOES.  It has access to the protected
     *  field elements <key> and <value>.
     */

    // CHAR-COUNT EXAMPLE:  Ignore the <key>, parse only the <value>
    MapperEmissionList EmissionList = new MapperEmissionList();
    for (int ii=0; ii<value.length(); ii++)
      EmissionList.add(
              new MapperEmission(value.substring(ii, ii + 1), 1) // emit the pair (ii-th character,1)
      );

    return EmissionList;
  }
}
