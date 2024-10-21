package com.sigpwned.inference4j.util;

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
}
