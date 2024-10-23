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

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class ListsTest {
  @Test
  public void givenEmptyInput_whenCartesianProduct_thenOneEmptyResult() {
    List<Set<Integer>> input = Collections.emptyList();
    List<Set<Integer>> expected = Collections.singletonList(new HashSet<>());

    List<Set<Integer>> result = Lists.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenSingleElementSets_whenCartesianProduct_thenOneResult() {
    List<Set<Integer>> input =
        Arrays.asList(Collections.singleton(1), Collections.singleton(2), Collections.singleton(3));

    List<Set<Integer>> expected = Collections.singletonList(new HashSet<>(Arrays.asList(1, 2, 3)));

    List<Set<Integer>> result = Lists.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenMultiElementSets_whenCartesianProduct_thenMultipleResults() {
    List<Set<Integer>> input = Arrays.asList(new HashSet<>(Arrays.asList(1, 2)),
        Collections.singleton(3), new HashSet<>(Arrays.asList(4, 5)));

    List<Set<Integer>> expected =
        Arrays.asList(new HashSet<>(Arrays.asList(1, 3, 4)), new HashSet<>(Arrays.asList(1, 3, 5)),
            new HashSet<>(Arrays.asList(2, 3, 4)), new HashSet<>(Arrays.asList(2, 3, 5)));

    List<Set<Integer>> result = Lists.cartesianProduct(input);
    assertEquals(expected, result);
  }

  @Test
  public void givenOneEmptyElementSet_whenCartesianProduct_thenNoResults() {
    List<Set<Integer>> input = Arrays.asList(new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(),
        new HashSet<>(Arrays.asList(4, 5)));

    List<Set<Integer>> expected = Collections.emptyList();

    List<Set<Integer>> result = Lists.cartesianProduct(input);
    assertEquals(expected, result);
  }
}
