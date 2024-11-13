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

public class AbductiveWalk<RuleIdT, PropositionT> {
  public static <RuleIdT, PropositionT> AbductiveWalk<RuleIdT, PropositionT> start(
      Set<PropositionT> hypotheses) {
    return new AbductiveWalk<>(hypotheses);
  }

  /**
   * The hypotheses that are being tested. These are the same as the hypotheses that are used to
   * start the walk and they never change.
   */
  private final Set<PropositionT> hypotheses;

  /**
   * The rules that have been fired. These are the rules that have been used to reach the current
   * state of the walk.
   */
  private final Set<Rule<RuleIdT, PropositionT>> fired;

  /**
   * The evidence that has been discovered so far. Disjoint from the hypotheses, even if the
   * hypotheses are later shown to be true via circular logic.
   * 
   * TODO Should we allow hypotheses in evidence?
   */
  private final Set<PropositionT> evidence;

  public AbductiveWalk(Set<PropositionT> hypotheses,
      LinkedHashSet<Rule<RuleIdT, PropositionT>> fired, Set<PropositionT> evidence) {
    this.hypotheses = unmodifiableSet(hypotheses);
    this.fired = unmodifiableSet(fired);
    this.evidence = unmodifiableSet(evidence);
  }

  public AbductiveWalk(Set<PropositionT> hypotheses) {
    this.hypotheses = unmodifiableSet(hypotheses);
    this.fired = Sets.of();
    this.evidence = Sets.of();
  }

  /**
   * @return the hypotheses
   */
  public Set<PropositionT> getHypotheses() {
    return hypotheses;
  }

  /**
   * @return the fired
   */
  public Set<Rule<RuleIdT, PropositionT>> getFired() {
    return fired;
  }

  /**
   * @return the evidence
   */
  public Set<PropositionT> getEvidence() {
    return evidence;
  }

  public AbductiveWalk<RuleIdT, PropositionT> step(Rule<RuleIdT, PropositionT> step) {
    if (step == null)
      throw new NullPointerException();

    if (getFired().contains(step))
      throw new IllegalArgumentException("rule already fired: " + step);

    if (step.getAntecedents().stream()
        .allMatch(a -> getEvidence().contains(a) || getHypotheses().contains(a)))
      throw new IllegalArgumentException("antecedents already discovered: " + step);

    if (!getHypotheses().contains(step.getConsequent())
        && !getEvidence().contains(step.getConsequent()))
      throw new IllegalArgumentException("consequent not satisfied: " + step);

    LinkedHashSet<Rule<RuleIdT, PropositionT>> newFired =
        new LinkedHashSet<>(getFired().size() + 1);
    newFired.addAll(getFired());
    newFired.add(step);

    LinkedHashSet<PropositionT> newEvidence = new LinkedHashSet<>(getEvidence().size() + 1);
    newEvidence.addAll(getEvidence());
    newEvidence.addAll(step.getAntecedents());

    return new AbductiveWalk<>(getHypotheses(), newFired, newEvidence);
  }

  public boolean contains(AbductiveWalk<RuleIdT, PropositionT> other) {
    return getHypotheses().containsAll(other.getHypotheses())
        && getFired().containsAll(other.getFired())
        && getEvidence().containsAll(other.getEvidence());
  }

  @Override
  public int hashCode() {
    return Objects.hash(evidence, fired, hypotheses);
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
    AbductiveWalk other = (AbductiveWalk) obj;
    return Objects.equals(evidence, other.evidence) && Objects.equals(fired, other.fired)
        && Objects.equals(hypotheses, other.hypotheses);
  }

  @Override
  public String toString() {
    return "AbductiveWalk [hypotheses=" + hypotheses + ", fired=" + fired + ", evidence=" + evidence
        + "]";
  }
}
