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
package com.sigpwned.inference4j;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.inference4j.util.MoreCollectors;
import com.sigpwned.inference4j.util.Sets;

/**
 * <p>
 * The result of performing iterative backwards chaining (abduction) steps starting from a set of
 * propositions (hypotheses) under a given set of rules. The result is a set of jointly sufficient
 * but perhaps not individually necessary propositions that entail the given hypotheses.
 * </p>
 * 
 * @param <RuleIdT>
 * @param <PropositionT>
 */
public class AbductiveClosure<RuleIdT, PropositionT> {
  /**
   * The initial propositions that were assumed to be true to start the walk. Each hypothesis in
   * this set will also appear in either {@code lemmas} or {@code postulates}, depending on whether
   * it is otherwise satsisfiable (i.e., can be inferred from other propositions under the given
   * rules), or unsatisfiable (i.e., cannot be inferred from other propositions under the given
   * rules), respectively.
   */
  private final Set<PropositionT> hypotheses;

  /**
   * The rule set that was used to perform the walk.
   */
  private final RuleSet<RuleIdT, PropositionT> rules;

  /**
   * The subset of rules from {@code rules} that were fired during the walk. Each individual rule
   * was fired exactly once.
   */
  private final Set<Rule<RuleIdT, PropositionT>> fired;

  /**
   * Propositions that must be assumed to be true for the given hypotheses to be true. When a
   * hypothesis appears in this set, it indicates that it cannot be derived under the given rules
   * and can only be true by assumption. If a proposition is a postulate, then it is not a lemma, by
   * definition, and vice versa.
   */
  private final Set<PropositionT> postulates;

  /**
   * Propositions that appear as the consequent of at least one of the fired rules, i.e., it is an
   * intermediate conclusion in at least one proof of at least one hypothesis. When a hypothesis
   * appears in this set, it indicates that it is derivable under the given rules. If a proposition
   * is a lemma, then it is not a postulate, by definition, and vice versa.
   */
  private final Set<PropositionT> lemmas;

  public AbductiveClosure(Set<PropositionT> hypotheses, RuleSet<RuleIdT, PropositionT> rules,
      Set<Rule<RuleIdT, PropositionT>> fired, Set<PropositionT> postulates,
      Set<PropositionT> lemmas) {
    this.hypotheses = unmodifiableSet(hypotheses);
    this.rules = requireNonNull(rules);
    this.fired = unmodifiableSet(fired);
    this.postulates = unmodifiableSet(postulates);
    this.lemmas = unmodifiableSet(lemmas);

    // The postulates and lemmas should be disjoint.
    assert Sets.disjoint(getPostulates(), getLemmas());

    // All hypotheses should be either postulates or lemmas.
    assert getHypotheses().stream()
        .allMatch(p -> getPostulates().contains(p) || getLemmas().contains(p));

    // These rules were fired, so their consequents must be lemmas.
    assert getFired().stream().allMatch(r -> getLemmas().contains(r.getConsequent()));

    // The postulates should not be the consequents of any fired rules.
    assert getPostulates().stream()
        .noneMatch(p -> getFired().stream().anyMatch(r -> r.getConsequent().equals(p)));

    // All rules should have unique IDs. Therefore, these rules should have unique IDs.
    assert getFired().stream().map(Rule::getId).collect(MoreCollectors.duplicates()).isEmpty();

    // Additionally:
    // - We don't check here, but no matching rule in rules should imply a postulate.
  }

  /**
   * @return the hypotheses
   */
  public Set<PropositionT> getHypotheses() {
    return hypotheses;
  }

  /**
   * @return the rules
   */
  public RuleSet<RuleIdT, PropositionT> getRules() {
    return rules;
  }

  /**
   * @return the fired
   */
  public Set<Rule<RuleIdT, PropositionT>> getFired() {
    return fired;
  }

  /**
   * @return the postulates
   */
  public Set<PropositionT> getPostulates() {
    return postulates;
  }

  /**
   * @return the lemmas
   */
  public Set<PropositionT> getLemmas() {
    return lemmas;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fired, hypotheses, lemmas, postulates, rules);
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
    AbductiveClosure other = (AbductiveClosure) obj;
    return Objects.equals(fired, other.fired) && Objects.equals(hypotheses, other.hypotheses)
        && Objects.equals(lemmas, other.lemmas) && Objects.equals(postulates, other.postulates)
        && Objects.equals(rules, other.rules);
  }

  @Override
  public String toString() {
    return "AbductiveClosure [hypotheses=" + hypotheses + ", rules=" + rules + ", fired=" + fired
        + ", postulates=" + postulates + ", lemmas=" + lemmas + "]";
  }
}
