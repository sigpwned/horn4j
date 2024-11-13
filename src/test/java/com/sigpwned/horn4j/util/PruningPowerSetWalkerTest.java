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
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class PruningPowerSetWalkerTest {
  @Test
  public void givenEmptySet_whenPrunedWalk_thenVisitEmptySetOnly() {
    PruningPowerSetWalker<String> walker = new PruningPowerSetWalker<>();
    Set<String> input = Sets.of();
    Set<Set<String>> visited = new HashSet<>();

    walker.prunedWalk(input, subset -> {
      visited.add(Sets.copyOf(subset));
      return false;
    });

    assertEquals(Sets.of(Sets.of()), visited);
  }

  @Test
  public void givenSingleElementSet_whenPrunedWalk_thenVisitEmptyAndSingleElementSet() {
    PruningPowerSetWalker<String> walker = new PruningPowerSetWalker<>();
    Set<String> input = Sets.of("a");
    Set<Set<String>> visited = new HashSet<>();

    walker.prunedWalk(input, subset -> {
      visited.add(Sets.copyOf(subset));
      return false;
    });

    assertEquals(Sets.of(Sets.of(), Sets.of("a")), visited);
  }

  @Test
  public void givenMultipleElementSet_whenPrunedWalk_thenVisitAllSubsets() {
    PruningPowerSetWalker<String> walker = new PruningPowerSetWalker<>();
    Set<String> input = Sets.of("a", "b");
    Set<Set<String>> visited = new HashSet<>();

    walker.prunedWalk(input, subset -> {
      visited.add(Sets.copyOf(subset));
      return false;
    });

    assertEquals(Sets.of(Sets.of(), Sets.of("a"), Sets.of("b"), Sets.of("a", "b")), visited);
  }

  @Test
  public void givenThreeElementSet_whenPrunedWalk_thenPruneSubsets() {
    PruningPowerSetWalker<String> walker = new PruningPowerSetWalker<>();
    Set<String> input = Sets.of("a", "b", "c");
    Set<Set<String>> visited = new HashSet<>();

    walker.prunedWalk(input, subset -> {
      visited.add(Sets.copyOf(subset));
      return subset.equals(Sets.of("a"));
    });

    assertEquals(Sets.of(Sets.of(), Sets.of("a"), Sets.of("b"), Sets.of("c"), Sets.of("b", "c")),
        visited);
  }

  @Test
  public void givenFourElementSet_whenPrunedWalk_thenPruneSubsets() {
    PruningPowerSetWalker<String> walker = new PruningPowerSetWalker<>();
    Set<String> input = Sets.of("a", "b", "c", "d");
    Set<Set<String>> visited = new HashSet<>();

    walker.prunedWalk(input, subset -> {
      visited.add(Sets.copyOf(subset));
      return subset.equals(Sets.of("a"));
    });

    assertEquals(
        Sets.of(Sets.of(), Sets.of("a"), Sets.of("b"), Sets.of("c"), Sets.of("d"),
            Sets.of("b", "c"), Sets.of("c", "d"), Sets.of("b", "d"), Sets.of("b", "c", "d")),
        visited);
  }
}
