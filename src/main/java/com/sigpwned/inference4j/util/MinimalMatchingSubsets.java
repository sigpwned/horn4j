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
package com.sigpwned.inference4j.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class MinimalMatchingSubsets {

  public static <P> List<Set<P>> findMinimalMatchingSubsets(Set<P> P, Predicate<Set<P>> T) {
    List<Set<P>> result = new ArrayList<>(); // List to store minimal matching subsets
    List<Set<P>> matchedSubsets = new ArrayList<>(); // Track matched subsets for pruning

    List<P> elements = new ArrayList<>(P); // Convert set P to a list for indexed access

    // Iterate over subset lengths from 1 to N (size of the set P)
    for (int k = 1; k <= P.size(); k++) {
      backtrack(new HashSet<>(), 0, k, elements, matchedSubsets, T, result);
    }

    return result;
  }

  private static <P> void backtrack(Set<P> currentSet, int start, int k, List<P> elements,
      List<Set<P>> matchedSubsets, Predicate<Set<P>> T, List<Set<P>> result) {
    // Base case: if the current set has reached the desired size
    if (currentSet.size() == k) {
      if (T.test(currentSet)) { // If the current set matches the predicate T
        result.add(new HashSet<>(currentSet)); // Store the matched set in result
        matchedSubsets.add(new HashSet<>(currentSet)); // Track this set to prune supersets
      }
      return;
    }

    // Iterate over the remaining elements to generate subsets dynamically
    for (int i = start; i < elements.size(); i++) {
      P nextElement = elements.get(i);
      currentSet.add(nextElement); // Add the next element to the current set

      // Only proceed if the current set is not a superset of any matched subset
      if (!isSupersetOfAny(currentSet, matchedSubsets)) {
        backtrack(currentSet, i + 1, k, elements, matchedSubsets, T, result);
      }

      currentSet.remove(nextElement); // Backtrack: remove the element and explore other
                                      // possibilities
    }
  }

  // Utility method to check if the current set is a superset of any previously matched subset
  private static <P> boolean isSupersetOfAny(Set<P> currentSet, List<Set<P>> matchedSubsets) {
    for (Set<P> matched : matchedSubsets) {
      if (currentSet.containsAll(matched)) {
        return true; // The current set is a superset of a previously matched set
      }
    }
    return false; // The current set is not a superset of any matched set
  }

  // Example usage
  public static void main(String[] args) {
    // Define a set P = {1, 2, 3, 4, 5}
    Set<Integer> P = new HashSet<>();
    P.add(1);
    P.add(2);
    P.add(3);
    P.add(4);
    P.add(5);

    // Define a predicate T that matches sets whose sum is even
    Predicate<Set<Integer>> T =
        subset -> subset.stream().mapToInt(Integer::intValue).sum() % 2 == 0;

    // Find minimal matching subsets
    List<Set<Integer>> minimalSubsets = findMinimalMatchingSubsets(P, T);

    // Print the result
    for (Set<Integer> subset : minimalSubsets) {
      System.out.println(subset);
    }
  }
}
