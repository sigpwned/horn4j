package com.sigpwned.inference4j.util;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

public final class MoreCollectors {
  private MoreCollectors() {}

  /**
   * Returns a collector that counts the number of occurrences of each element.
   * 
   * @param <T> the type of the input elements
   * @return a collector that counts the number of occurrences of each element
   */
  public static <T> Collector<T, ?, Map<T, Long>> frequencies() {
    return collectingAndThen(groupingBy(Function.identity(), LinkedHashMap::new, counting()),
        Collections::unmodifiableMap);
  }

  /**
   * Returns a collector that returns a map containing only the elements that occur more than once,
   * and the number of occurrences of each.
   * 
   * @param <T> the type of the input elements
   * @return a collector that returns a map containing only the elements that occur more than once,
   *         and the number of occurrences of each.
   */
  public static <T> Collector<T, ?, Map<T, Long>> duplicates() {
    return collectingAndThen(frequencies(), as -> {
      Map<T, Long> result = new LinkedHashMap<>(as);
      Iterator<Map.Entry<T, Long>> iterator = result.entrySet().iterator();
      while (iterator.hasNext()) {
        if (iterator.next().getValue() == 1L) {
          iterator.remove();
        }
      }
      return unmodifiableMap(result);
    });
  }
}
