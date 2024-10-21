package com.sigpwned.inference4j.util;

import static java.util.Collections.unmodifiableCollection;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Implements the chain of responsibility pattern.
 */
public abstract class Chain<T> implements Iterable<T> {
  private final Deque<T> elements = new ArrayDeque<>();

  public void addFirst(T element) {
    elements.addFirst(element);
  }

  public void addLast(T element) {
    elements.addLast(element);
  }

  @Override
  public Iterator<T> iterator() {
    return unmodifiableCollection(elements).iterator();
  }

  public Stream<T> stream() {
    return elements.stream();
  }
}
