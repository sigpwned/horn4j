/*-
 * =================================LICENSE_START==================================
 * horn4j
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
package com.sigpwned.horn4j;

import static java.util.Collections.unmodifiableSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.horn4j.util.Sets;

/**
 * Represents a deductive walk starting with a set of assumptions and iteratively applying rules.
 * 
 * @param <RuleIdT>
 * @param <PropositionT>
 */
public class DeductiveWalk<RuleIdT, PropositionT> {
  public static <RuleIdT, PropositionT> DeductiveWalk<RuleIdT, PropositionT> start(
      Set<PropositionT> assumptions) {
    return new DeductiveWalk<>(assumptions);
  }

  /**
   * The assumptions made at the start of the walk. They never change.
   */
  private final Set<PropositionT> assumptions;

  /**
   * The rules that have been fired so far.
   */
  private final Set<Rule<RuleIdT, PropositionT>> fired;

  /**
   * The conclusions made so far. Disjoint from the assumptions, even if the assumptions are shown
   * to be true later by circular reasoning.
   */
  private final Set<PropositionT> conclusions;

  public DeductiveWalk(Set<PropositionT> assumptions,
      LinkedHashSet<Rule<RuleIdT, PropositionT>> fired, Set<PropositionT> conclusions) {
    this.assumptions = unmodifiableSet(assumptions);
    this.fired = unmodifiableSet(fired);
    this.conclusions = unmodifiableSet(conclusions);
  }

  public DeductiveWalk(Set<PropositionT> assumptions) {
    this.assumptions = unmodifiableSet(assumptions);
    this.fired = Sets.of();
    this.conclusions = Sets.of();
  }

  /**
   * @return the assumptions
   */
  public Set<PropositionT> getAssumptions() {
    return assumptions;
  }

  /**
   * @return the rules that were fired during the walk, in order.
   */
  public Set<Rule<RuleIdT, PropositionT>> getFired() {
    return fired;
  }

  /**
   * @return the conclusions
   */
  public Set<PropositionT> getConclusions() {
    return conclusions;
  }

  public DeductiveWalk<RuleIdT, PropositionT> step(Rule<RuleIdT, PropositionT> step) {
    if (step == null)
      throw new NullPointerException();

    if (getFired().contains(step))
      throw new IllegalArgumentException("rule already fired: " + step);

    if (!step.getAntecedents().stream()
        .allMatch(a -> getAssumptions().contains(a) || getConclusions().contains(a)))
      throw new IllegalArgumentException("missing antecedents: " + step);

    if (getAssumptions().contains(step.getConsequent()))
      throw new IllegalArgumentException("consequent already in assumptions: " + step);
    if (getConclusions().contains(step.getConsequent()))
      throw new IllegalArgumentException("consequent already in conclusions: " + step);

    LinkedHashSet<Rule<RuleIdT, PropositionT>> newFired =
        new LinkedHashSet<>(getFired().size() + 1);
    newFired.addAll(getFired());
    newFired.add(step);

    LinkedHashSet<PropositionT> newConclusions = new LinkedHashSet<>(getConclusions().size() + 1);
    newConclusions.addAll(getConclusions());
    newConclusions.add(step.getConsequent());

    return new DeductiveWalk<>(getAssumptions(), newFired, newConclusions);
  }

  public boolean contains(DeductiveWalk<RuleIdT, PropositionT> other) {
    return getAssumptions().containsAll(other.getAssumptions())
        && getConclusions().containsAll(other.getConclusions())
        && getFired().containsAll(other.getFired());
  }

  @Override
  public int hashCode() {
    return Objects.hash(assumptions, conclusions, fired);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DeductiveWalk other = (DeductiveWalk) obj;
    return Objects.equals(assumptions, other.assumptions)
        && Objects.equals(conclusions, other.conclusions) && Objects.equals(fired, other.fired);
  }

  @Override
  public String toString() {
    return "DeductiveWalk [assumptions=" + assumptions + ", fired=" + fired + ", conclusions="
        + conclusions + "]";
  }
}
