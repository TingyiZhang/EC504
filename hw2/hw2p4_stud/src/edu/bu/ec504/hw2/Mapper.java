package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Mapper.MapperEmissionList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Abstract implementation of a threadable MAP-capable object.
 * @author Ari Trachtenberg
 */

public abstract class Mapper implements Callable<MapperEmissionList> {

  // CONSTRUCTORS

   /**
   * @param theKey The key input for the map
   * @param theValue The value input for the map
   */
   protected Mapper(Integer theKey, String theValue) {
    init(theKey, theValue);
  }

  // default constructor
  protected Mapper() {
  }

/**
 * Initialize key and value associated with this object.
 * *MUST* be called before any other methods.
 * @param theKey The key input for the map
 * @param theValue The value input for the map
 */
  public void init(Integer theKey, String theValue) {
    key = theKey;
    value = theValue;
  }
  // NESTED CLASS

  /**
   * Mapper-specific version of the EmissionTemplate
   */
  public static class MapperEmission extends EmissionTemplate {
    public MapperEmission(String theKey, Integer theValue) {
      super(theKey, theValue);
    }
  }

  /**
   * A list of {@link MapperEmission}s.
   */
  public static class MapperEmissionList extends ArrayList<MapperEmission> {}

  /**
   * This does the actual work of your mapper.
   * @return A list of emissions (i.e. key=String, value=Integer pairs) produced by this task.
   */
  @Override
  abstract public MapperEmissionList call();

  // ... STATIC METHODS
  //     (getters and setters)
  public static List<Mapper> getTasks() {
    return tasks;
  }
  public static void addTask(Mapper task) {
    Mapper.tasks.add(task);
  }
  public static List<Future<MapperEmissionList>> getFutures() {
    return futures;
  }
  public static void setFutures(
      List<Future<MapperEmissionList>> futures) {
    Mapper.futures = futures;
  }

  // STATIC FIELDS
  static final List<Mapper> tasks = new ArrayList<>();                  // all the tasks to be executed
  static List<Future<MapperEmissionList>> futures = new ArrayList<>();  // results of task executions

  // FIELDS
  protected Integer key=null;   // The key provided when constructing this object.
  protected String value=null;  // The value provided when constructing this object.
}
