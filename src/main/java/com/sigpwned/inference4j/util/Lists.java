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

public final class Lists {
  private Lists() {}

  /**
   * Computes the Cartesian product of a list of sets.
   *
   * <p>
   * The Cartesian product of a list of sets is a list containing all possible combinations where
   * each combination includes one element from each set. Each combination is represented as a set
   * of elements.
   * </p>
   *
   * @param <X> the type of elements in the sets
   * @param sets the input list of sets
   * @return a list of sets, each set being a unique combination of elements from the input sets
   */
  public static <X> List<Set<X>> cartesianProduct(List<Set<X>> sets) {
    // Start with a list containing an empty set
    List<Set<X>> result = new ArrayList<>();
    result.add(new HashSet<X>());

    // Iterate over each set in the input list
    for (Set<X> currentSet : sets) {
      List<Set<X>> temp = new ArrayList<>();
      // For each existing combination in result
      for (Set<X> resSet : result) {
        // For each element in the current set
        for (X element : currentSet) {
          // Create a new set that adds the element to the existing combination
          Set<X> newSet = new HashSet<>(resSet);
          newSet.add(element);
          temp.add(newSet);
        }
      }
      // Update result with the new combinations
      result = temp;
    }
    return result;
  }
}
