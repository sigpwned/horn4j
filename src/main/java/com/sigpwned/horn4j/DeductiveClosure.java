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
package com.sigpwned.horn4j;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.horn4j.util.MoreCollectors;
import com.sigpwned.horn4j.util.Sets;

/**
 * Represents a deductive closure, which is a set of propositions assumed to be true
 * ("assumptions"), inference rules ("rules"), and propositions concluded to be true from the
 * original assumptions and inference rules ("conclusions"). The conclusions are the propositions
 * that are entailed by the assumptions under the given rules. (That is, the conclusions are the
 * propositions for which the assumptions are jointly sufficient but perhaps not individually
 * necessary to prove them.)
 * 
 * @param <RuleIdT>
 * @param <PropositionT>
 * @param <Rule<RuleIdT, PropositionT>>
 */
public class DeductiveClosure<RuleIdT, PropositionT> {
  /**
   * The propositions assumed to be true at the beginning of the walk.
   */
  private final Set<PropositionT> assumptions;

  /**
   * The set of rules that were used to perform the walk.
   */
  private final RuleSet<RuleIdT, PropositionT> rules;

  /**
   * The set of rules that were fired during the walk. Every element of this set must be in the
   * rules set.
   */
  private final Set<Rule<RuleIdT, PropositionT>> fired;

  /**
   * <p>
   * The propositions that were inferred (i.e., appeared as the concesequent of at least one fired
   * rule) by the end of the walk.
   * </p>
   * 
   * <p>
   * The conclusions may contain assumptions, if they can also be inferred from the given
   * assumptions and rules. This may or may not imply a cycle.
   * 
   * <p>
   * For one example, given assumptions {@code a} and {@code b} plus a rule {@code (a)} &rarr;
   * {@code b}, {@code b} would be an assumption and a conclusion, even though there is no circular
   * logic.
   * </p>
   * 
   * <p>
   * For another example, consider the assumptions {@code a} and {@code b} plus the rules
   * {@code (a)} &rarr; {@code b} and {@code (b)} &rarr; {@code a}. In this case, {@code a} and
   * {@code b} would be both assumptions and conclusions, and there would be circular logic.
   * </p>
   * 
   */
  private final Set<PropositionT> conclusions;

  public DeductiveClosure(Set<PropositionT> assumptions, RuleSet<RuleIdT, PropositionT> rules,
      Set<Rule<RuleIdT, PropositionT>> fired, Set<PropositionT> conclusions) {
    this.assumptions = unmodifiableSet(assumptions);
    this.rules = requireNonNull(rules);
    this.fired = unmodifiableSet(fired);
    this.conclusions = unmodifiableSet(conclusions);

    // The given rules were fired, so their antecedents must have been satisfied. The antecedents of
    // each rule are either assumptions or conclusions.
    assert getFired().stream()
        .allMatch(r -> Sets.union(getAssumptions(), conclusions).containsAll(r.getAntecedents()));

    // The given rules were fired, so their consequents must be conclusions.
    assert getFired().stream().allMatch(r -> getConclusions().contains(r.getConsequent()));

    // All rules should have unique IDs. Therefore, these rules should have unique IDs.
    assert getFired().stream().map(Rule::getId).collect(MoreCollectors.duplicates()).isEmpty();

    // Additionally:
    // - Assumptions can be conclusions, if some subset of assumptions entails another assumption.
    // - We don't check the math here, but the assumptions should entail all conclusions.
  }

  /**
   * @return the assumptions
   */
  public Set<PropositionT> getAssumptions() {
    return assumptions;
  }

  /**
   * @return the rules
   */
  public RuleSet<RuleIdT, PropositionT> getRules() {
    return rules;
  }

  /**
   * @return the rules that were fired
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

  @Override
  public int hashCode() {
    return Objects.hash(assumptions, conclusions, fired, rules);
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
    DeductiveClosure other = (DeductiveClosure) obj;
    return Objects.equals(assumptions, other.assumptions)
        && Objects.equals(conclusions, other.conclusions) && Objects.equals(fired, other.fired)
        && Objects.equals(rules, other.rules);
  }

  @Override
  public String toString() {
    return "DeductiveClosure [assumptions=" + assumptions + ", rules=" + rules + ", fired=" + fired
        + ", conclusions=" + conclusions + "]";
  }
}
