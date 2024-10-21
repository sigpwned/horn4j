package com.sigpwned.inference4j.util;

import static java.util.Collections.unmodifiableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Sets {
  private Sets() {}

  /**
   * Compute the Cartesian product of the given sets. That is, return the set of sets that can be
   * formed by choosing one element from each of the given sets. Inspired by Guava's
   * {@code Sets#cartesianProduct(Set...)}.
   * 
   * @param <T> the type of elements in the sets
   * @param sets the sets to compute the Cartesian product of
   * @return the Cartesian product of the given sets
   * @throws NullPointerException if {@code sets} is {@code null}
   */
  public static <T> Set<Set<T>> cartesianProduct(Set<Set<T>> sets) {
    if (sets == null)
      throw new NullPointerException();

    // Convert the input set of sets to a list to maintain a consistent order
    Set<Set<T>> setOfSets = new HashSet<>(sets);
    // Start with a set containing an empty set
    Set<Set<T>> result = new HashSet<>();
    result.add(new HashSet<T>());

    // Iterate over each set in the list
    for (Set<T> currentSet : setOfSets) {
      Set<Set<T>> temp = new HashSet<>();
      // Build new sets by adding each element of the current set to each set in the result
      for (Set<T> resSet : result) {
        for (T element : currentSet) {
          Set<T> newSet = new HashSet<>(resSet);
          newSet.add(element);
          temp.add(newSet);
        }
      }
      result = temp;
    }

    return result;
  }

  /**
   * Compute the union of the given sets. That is, return the set of elements that are in any of the
   * given sets. Inspired by Guava's {@code Sets#union(Set...)}.
   * 
   * @param <T> the type of elements in the sets
   * @param sets the sets to compute the union of
   * @return the union of the given sets
   * @throws NullPointerException if {@code sets} is {@code null}
   */
  public static <T> Set<T> union(Collection<Set<T>> sets) {
    if (sets == null)
      throw new NullPointerException();
    return unmodifiableSet(
        sets.stream().flatMap(Set::stream).collect(HashSet::new, Set::add, Set::addAll));
  }

  /**
   * Returns true if the given sets are disjoint, i.e., if they have no elements in common, or false
   * otherwise.
   * 
   * @param <T> the type of elements in the sets
   * @param xs the first set
   * @param ys the second set
   * @return true if the given sets are disjoint, or false otherwise
   * @throws NullPointerException if {@code xs} or {@code ys} is {@code null}
   */
  public static boolean disjoint(Set<?> xs, Set<?> ys) {
    if (xs == null)
      throw new NullPointerException();
    if (ys == null)
      throw new NullPointerException();
    return xs.stream().noneMatch(ys::contains);
  }
}
