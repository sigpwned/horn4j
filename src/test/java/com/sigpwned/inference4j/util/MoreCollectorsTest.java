package com.sigpwned.inference4j.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MoreCollectorsTest {
  @Test
  public void givenEmptyList_whenFrequencies_thenReturnEmptyMap() {
    List<String> input = Collections.emptyList();
    Map<String, Long> result = input.stream().collect(MoreCollectors.frequencies());
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenListWithSingleElement_whenFrequencies_thenReturnSingleEntryMap() {
    List<String> input = Arrays.asList("a");
    Map<String, Long> result = input.stream().collect(MoreCollectors.frequencies());
    assertEquals(Map.of("a", 1L), result);
  }

  @Test
  public void givenListWithMultipleElements_whenFrequencies_thenReturnCorrectCounts() {
    List<String> input = Arrays.asList("a", "b", "a", "c", "b", "a");
    Map<String, Long> result = input.stream().collect(MoreCollectors.frequencies());
    assertEquals(Map.of("a", 3L, "b", 2L, "c", 1L), result);
  }

  @Test
  public void givenEmptyList_whenDuplicates_thenReturnEmptyMap() {
    List<String> input = Collections.emptyList();
    Map<String, Long> result = input.stream().collect(MoreCollectors.duplicates());
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenListWithNoDuplicates_whenDuplicates_thenReturnEmptyMap() {
    List<String> input = Arrays.asList("a", "b", "c");
    Map<String, Long> result = input.stream().collect(MoreCollectors.duplicates());
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenListWithDuplicates_whenDuplicates_thenReturnCorrectCounts() {
    List<String> input = Arrays.asList("a", "b", "a", "c", "b", "a");
    Map<String, Long> result = input.stream().collect(MoreCollectors.duplicates());
    assertEquals(Map.of("a", 3L, "b", 2L), result);
  }

  @Test
  public void givenListWithAllDuplicates_whenDuplicates_thenReturnAllElements() {
    List<String> input = Arrays.asList("a", "a", "b", "b", "c", "c");
    Map<String, Long> result = input.stream().collect(MoreCollectors.duplicates());
    assertEquals(Map.of("a", 2L, "b", 2L, "c", 2L), result);
  }
}
