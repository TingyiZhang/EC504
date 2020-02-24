package edu.bu.ec504.hw0p4;

import java.util.NoSuchElementException;

public interface FindSimilar {

  /**
   * Adds {@code entry} to the end of the object's internal dictionary.
   * @param entry A string to add to the dictionary
   * @return The new index of {@code entry} in the dictionary.
   */
  int addToDictionary(String entry);

  /**
   * Find an entry in the object's internal dictionary, if it exists.
   * If it does not exist, throws a {@link NoSuchElementException}.
   * @param entry The entry to find in the internal dictionary.
   * @return An index for the {@code entry} in the internal dictionary.
   * @throws NoSuchElementException if {@code entry} is not in the internal dictionary of the object.
   */
  int getIndex(String entry) throws NoSuchElementException;

  /**
   * Finds the word closest to {@code word} in the internal dictionary of the object.
   * The distance between a dictionary entry w' and {@code word} is defined in the homework zero
   * problem statement.
   * (See {@linktourl https://algorithmics.bu.edu/fw/EC504_admin/HomeworkZero})
   * @param word The word to look for in the dictionary.
   * @return An index in the dictionary of the entry closest to {@code word}.
   */
  int closestWord(String word);
}
