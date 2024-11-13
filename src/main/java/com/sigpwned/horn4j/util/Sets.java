/*-
 * =================================LICENSE_START==================================
 * inference4j
 * ====================================SECTION=====================================
 * Copyright (C) 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.horn4j.util;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Sets {
  private Sets() {}

  public static <T> Set<T> of() {
    return emptySet();
  }

  public static <T> Set<T> of(T value) {
    return singleton(value);
  }

  @SafeVarargs
  public static <T> Set<T> of(T... values) {
    if (values == null)
      throw new NullPointerException();
    if (values.length == 0)
      return of();
    if (values.length == 1)
      return of(values[0]);
    Set<T> result = new HashSet<>(values.length);
    for (T value : values)
      result.add(value);
    return result;
  }

  public static <T> Set<T> copyOf(Collection<? extends T> elements) {
    return unmodifiableSet(new HashSet<>(elements));
  }

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
   * Compute the union of the given sets. That is, return the set of elements that are in any of the
   * given sets. Inspired by Guava's {@code Sets#union(Set...)}.
   * 
   * @param <T> the type of elements in the sets
   * @param sets the sets to compute the union of
   * @return the union of the given sets
   * @throws NullPointerException if {@code sets} is {@code null}
   */
  public static <T> Set<T> union(Set<T> xs, Set<T> ys) {
    if (xs == null)
      throw new NullPointerException();
    if (ys == null)
      throw new NullPointerException();
    return union(Lists.of(xs, ys));
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

  public static <T> LinkedHashSet<T> newLinkedHashSet(Collection<T> elements) {
    LinkedHashSet<T> result = new LinkedHashSet<>();
    for (T element : elements)
      result.add(element);
    return result;
  }

  @SafeVarargs
  public static <T> LinkedHashSet<T> newLinkedHashSet(T... elements) {
    return newLinkedHashSet(Lists.of(elements));
  }
}
