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
import java.util.Objects;
import java.util.Set;

public class Argument<RuleIdT, PropositionT> {
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static final Argument VACUOUS = new Argument(Set.of(), Set.of());

  @SuppressWarnings("unchecked")
  public static <RuleIdT, PropositionT> Argument<RuleIdT, PropositionT> vacuous() {
    return VACUOUS;
  }

  /**
   * The propositions assumed to be true as the basis for this argument.
   */
  private final Set<PropositionT> assumptions;

  /**
   * The rules that are used to derive new propositions in this argument.
   */
  private final Set<Rule<RuleIdT, PropositionT>> rules;

  public Argument(Set<PropositionT> assumptions, Set<Rule<RuleIdT, PropositionT>> rules) {
    this.assumptions = unmodifiableSet(assumptions);
    this.rules = unmodifiableSet(rules);

    // The assumptions should not appear as a consequent in the rules
    assert assumptions.stream()
        .noneMatch(a -> rules.stream().anyMatch(r -> r.getConsequent().equals(a)));

    // Additionally:
    // - We don't check, but the antecedents of each rule should be either (a) an assumption, or (b)
    // the consequent of another rule "earlier" in the argument.
  }

  /**
   * Returns true if this argument is vacuous (i.e. has no assumptions and no rules), or false
   * otherwise.
   */
  public boolean isVacuous() {
    return getAssumptions().isEmpty() && getRules().isEmpty();
  }

  /**
   * Returns true if this argument is a superset of the given argument, i.e. if all the assumptions
   * and rules of the given argument are also present in this argument.
   * 
   * @param that
   * @return
   */
  public boolean isSupersetOf(Argument<RuleIdT, PropositionT> that) {
    return this.getAssumptions().containsAll(that.getAssumptions())
        && this.getRules().containsAll(that.getRules());
  }

  public boolean isSubsetOf(Argument<RuleIdT, PropositionT> that) {
    return that.getAssumptions().containsAll(this.getAssumptions())
        && that.getRules().containsAll(this.getRules());
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
  public Set<Rule<RuleIdT, PropositionT>> getRules() {
    return rules;
  }

  @Override
  public int hashCode() {
    return Objects.hash(assumptions, rules);
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
    Argument other = (Argument) obj;
    return Objects.equals(assumptions, other.assumptions) && Objects.equals(rules, other.rules);
  }

  @Override
  public String toString() {
    return "Argument [assumptions=" + assumptions + ", rules=" + rules + "]";
  }


}
