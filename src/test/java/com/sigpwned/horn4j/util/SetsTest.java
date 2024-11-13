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

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class SetsTest {
  @Test
  public void givenEmptyInput_whenCartesianProduct_thenOneEmptyResult() {
    Set<Set<Integer>> input = new HashSet<>();
    Set<Set<Integer>> expected = new HashSet<>();
    expected.add(new HashSet<>()); // The cartesian product of an empty set is a set containing the
                                   // empty set

    Set<Set<Integer>> result = Sets.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenSingleElementSets_whenCartesianProduct_thenOneResult() {
    Set<Set<Integer>> input = new HashSet<>();
    input.add(Collections.singleton(1));
    input.add(Collections.singleton(2));
    input.add(Collections.singleton(3));

    Set<Set<Integer>> expected = new HashSet<>();
    Set<Integer> combinedSet = new HashSet<>(Arrays.asList(1, 2, 3));
    expected.add(combinedSet);

    Set<Set<Integer>> result = Sets.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenMultiElementSets_whenCartesianProduct_thenMultipleResults() {
    Set<Set<Integer>> input = new HashSet<>();
    input.add(new HashSet<>(Arrays.asList(1, 2)));
    input.add(new HashSet<>(Arrays.asList(3)));
    input.add(new HashSet<>(Arrays.asList(4, 5)));

    Set<Set<Integer>> expected = new HashSet<>();
    expected.add(new HashSet<>(Arrays.asList(1, 3, 4)));
    expected.add(new HashSet<>(Arrays.asList(1, 3, 5)));
    expected.add(new HashSet<>(Arrays.asList(2, 3, 4)));
    expected.add(new HashSet<>(Arrays.asList(2, 3, 5)));

    Set<Set<Integer>> result = Sets.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenOneEmptyElementSet_whenCartesianProduct_thenNoResults() {
    Set<Set<Integer>> input = new HashSet<>();
    input.add(new HashSet<>(Arrays.asList(1, 2)));
    input.add(new HashSet<>(Arrays.asList()));
    input.add(new HashSet<>(Arrays.asList(4, 5)));

    Set<Set<Integer>> expected = new HashSet<>();

    Set<Set<Integer>> result = Sets.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenTwoSetsWithNonEmptyIntersection_whenDifference_thenGetDifference() {
    Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
    Set<Integer> set2 = new HashSet<>(Arrays.asList(2, 3, 4));
    Set<Integer> expected = new HashSet<>(Arrays.asList(1));

    Set<Integer> result = Sets.difference(set1, set2);
    assertEquals(expected, result);
  }
}
