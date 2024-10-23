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

import static java.util.Collections.unmodifiableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs a pruned walk of the power set of a given set, invoking a visitor for each subset. The
 * visitor's response can prune the search space by skipping supersets of previously visited
 * subsets.
 * 
 * @param <T> the type of elements in the set
 */
public class PruningPowerSetWalker<T> {
  /**
   * Visitor interface for handling elements of the given set's power set.
   */
  @FunctionalInterface
  public static interface Visitor<T> {
    /**
     * <p>
     * Invoked for elements of the power set of the given set. If this method returns {@code true}
     * for a particular subset "x", then the walker will not visit any remaining subsets of the
     * given set that are also supersets of "x". Otherwise, the walker will continue visiting
     * supersets of "x". In any case, the walker will only invoke this method for each subset at
     * most once.
     * </p>
     * 
     * <p>
     * For reasons of efficiency, the given subset is unmodifiable, but not immutable. The visitor
     * should not attempt to modify the subset. If it wishes to retain the subset, it should make a
     * copy.
     * </p>
     * 
     * @param subset an element of the power set of the given set
     * 
     * @return {@code true} to prune subsequent supersets of this subset, or {@code false} not to
     *         prune them
     */
    public boolean visit(Set<T> subset);
  }

  /**
   * Walks the power set of the given set, calling the given {@link Visitor visitor} for each
   * element. The first element visited will be the empty set, followed by subsets of size 1, then
   * subsets of size 2, and so on, up to the full set. If the visitor returns {@code true} for a
   * particular subset "x", then the walker will not visit any remaining subsets of the given set
   * that are also supersets of "x". This pruning is done in such a way that the underlying walk is
   * substantially more efficient than a naive power set walk, so it's safe to walk even very large
   * sets if the caller expects to prune many subsets.
   * 
   * @param xs the set to walk
   * @param handler the visitor to call for each subset
   */
  public void prunedWalk(Set<T> xs, Visitor<T> handler) {
    if (xs == null)
      throw new NullPointerException();
    if (handler == null)
      throw new NullPointerException();

    // First, try the empty set. If that returns true, then we should not proceed with the rest of
    // the power set walk, since any other set is a superset of the empty set.
    boolean shouldPruneEmptySet = handler.visit(Set.of());
    if (shouldPruneEmptySet) {
      return;
    }

    // If the input set is empty, then we've already visited all subsets.
    if (xs.size() == 0) {
      return;
    }

    if (xs.size() == 1) {
      // If the input set has only one element, then just visit the set and be done.
      boolean shouldPruneSingletonSet = handler.visit(xs);
      if (shouldPruneSingletonSet) {
        // I don't actually care, in this case. There are no more visits to do.
      }
      return;
    }

    // Just a rename of xs for clarity
    final Set<T> originalSet = xs;

    // List to store matched subsets that should prune their supersets.
    List<Set<T>> matchedSubsets = new ArrayList<>();

    // Do a walk of elements of size 1. We do this explicitly because sets of size 1 have a special
    // property: If the handler returns true for a set of size 1, then we can just remove it from
    // the set of elements to consider, since we don't need to visit any supersets of that set, and
    // any set that contains that element will be a superset of the set of size 1.
    Set<T> prunedSet = new HashSet<>(xs.size());
    for (T element : originalSet) {
      Set<T> singletonSet = Set.of(element);
      boolean shouldPruneSingletonSet = handler.visit(singletonSet);
      if (shouldPruneSingletonSet) {
        matchedSubsets.add(singletonSet);
      } else {
        prunedSet.add(element);
      }
    }

    // Convert the newly-pruned set to a list for easier indexed access
    List<T> prunedList = List.copyOf(prunedSet);

    // Walk subsets of increasing lengths from 2 to |originalSet|. We start at 2 because we already
    // visited subsets of size 1 above.
    for (int k = 2; k <= xs.size(); k++) {
      backtrack(new HashSet<>(), 0, k, prunedList, matchedSubsets, handler);
    }
  }

  /**
   * Backtracking function to generate subsets dynamically and invoke the handler. It prunes any
   * supersets of previously matched subsets (where the handler returned true).
   */
  private void backtrack(Set<T> currentSet, int start, int k, List<T> elements,
      List<Set<T>> matchedSubsets, Visitor<T> handler) {
    // Base case: if the current set has reached the desired size `k`
    if (currentSet.size() == k) {
      // Call the handler with the current subset. If the handler returns true, prune supersets
      // of this subset.
      boolean shouldPrune = handler.visit(unmodifiableSet(currentSet));
      if (shouldPrune) {
        matchedSubsets.add(Set.copyOf(currentSet));
      }
    } else {
      // Iterate over the remaining elements to generate subsets dynamically
      for (int i = start; i < elements.size(); i++) {
        // Get the next element to add to the current set
        T nextElement = elements.get(i);

        // Add the next element to the current set
        currentSet.add(nextElement);
        try {
          // Only proceed if the current set is not a superset of any matched subset
          if (!matchedSubsets.stream()
              .anyMatch(matchedSubset -> currentSet.containsAll(matchedSubset))) {
            backtrack(currentSet, i + 1, k, elements, matchedSubsets, handler);
          }
        } finally {
          // Remove the next element from the current set to backtrack
          currentSet.remove(nextElement);
        }
      }
    }
  }
}
