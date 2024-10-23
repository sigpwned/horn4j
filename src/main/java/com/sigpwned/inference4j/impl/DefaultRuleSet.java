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
package com.sigpwned.inference4j.impl;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;

/**
 * An immutable {@link RuleSet} that contains concrete rules provided eagerly at construction time.
 * 
 * @param <RuleIdT> The type of the rule identifiers
 * @param <PropositionT> The type of the propositions
 */
public class DefaultRuleSet<RuleIdT, PropositionT> implements RuleSet<RuleIdT, PropositionT> {
  private static class RuleSignature<PropositionT> {
    public static <PropositionT> RuleSignature<PropositionT> fromRule(Rule<?, PropositionT> rule) {
      return new RuleSignature<>(rule.getAntecedents(), rule.getConsequent());
    }

    private final Set<PropositionT> antecedents;
    private final PropositionT consequent;

    public RuleSignature(Set<PropositionT> antecedents, PropositionT consequent) {
      this.antecedents = requireNonNull(antecedents);
      this.consequent = requireNonNull(consequent);
    }

    @Override
    public int hashCode() {
      return Objects.hash(antecedents, consequent);
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
      RuleSignature other = (RuleSignature) obj;
      return Objects.equals(antecedents, other.antecedents)
          && Objects.equals(consequent, other.consequent);
    }

    @Override
    public String toString() {
      return "RuleSignature [antecedents=" + antecedents + ", consequent=" + consequent + "]";
    }
  }

  private final List<Rule<RuleIdT, PropositionT>> rules;
  private transient Map<PropositionT, Set<Rule<RuleIdT, PropositionT>>> rulesByConsequent;
  private transient Map<Set<PropositionT>, Set<Rule<RuleIdT, PropositionT>>> rulesByAntecedents;
  private transient Map<RuleSignature<PropositionT>, Set<Rule<RuleIdT, PropositionT>>> rulesBySignature;

  public DefaultRuleSet(List<Rule<RuleIdT, PropositionT>> rules) {
    this.rules = unmodifiableList(rules);
  }

  @Override
  public Set<Rule<RuleIdT, PropositionT>> findByExactAntecedents(Set<PropositionT> propositions) {
    return getRulesByAntecedents().getOrDefault(propositions, Collections.emptySet());
  }

  @Override
  public Set<Rule<RuleIdT, PropositionT>> findBySatisfiedAntecedents(
      Set<PropositionT> propositions) {
    return getRulesByAntecedents().entrySet().stream()
        .filter(e -> propositions.containsAll(e.getKey())).map(Map.Entry::getValue)
        .flatMap(Set::stream).collect(toSet());

  }

  @Override
  public Set<Rule<RuleIdT, PropositionT>> findByConsequent(PropositionT proposition) {
    return getRulesByConsequent().getOrDefault(proposition, Collections.emptySet());
  }

  @Override
  public Set<Rule<RuleIdT, PropositionT>> findBySignature(Set<PropositionT> antecedents,
      PropositionT consequent) {
    return getRulesBySignature().getOrDefault(new RuleSignature<>(antecedents, consequent),
        Collections.emptySet());
  }

  private List<Rule<RuleIdT, PropositionT>> getRules() {
    return rules;
  }

  /**
   * @return the rulesByConsequent
   */
  private Map<PropositionT, Set<Rule<RuleIdT, PropositionT>>> getRulesByConsequent() {
    if (rulesByConsequent == null) {
      rulesByConsequent = getRules().stream().collect(collectingAndThen(
          groupingBy(Rule::getConsequent,
              collectingAndThen(toSet(),
                  xs -> xs.size() == 1 ? singleton(xs.iterator().next()) : unmodifiableSet(xs))),
          Collections::unmodifiableMap));
    }
    return rulesByConsequent;
  }

  /**
   * @return the rulesByAntecedents
   */
  private Map<Set<PropositionT>, Set<Rule<RuleIdT, PropositionT>>> getRulesByAntecedents() {
    if (rulesByAntecedents == null) {
      rulesByAntecedents = getRules().stream().collect(collectingAndThen(
          groupingBy(Rule::getAntecedents,
              collectingAndThen(toSet(),
                  xs -> xs.size() == 1 ? singleton(xs.iterator().next()) : unmodifiableSet(xs))),
          Collections::unmodifiableMap));
    }
    return rulesByAntecedents;
  }

  /**
   * @return the rulesBySignature
   */
  private Map<RuleSignature<PropositionT>, Set<Rule<RuleIdT, PropositionT>>> getRulesBySignature() {
    if (rulesBySignature == null) {
      rulesBySignature = getRules().stream().collect(collectingAndThen(
          groupingBy(RuleSignature::fromRule,
              collectingAndThen(toSet(),
                  xs -> xs.size() == 1 ? singleton(xs.iterator().next()) : unmodifiableSet(xs))),
          Collections::unmodifiableMap));
    }
    return rulesBySignature;
  }

  @Override
  public int hashCode() {
    return Objects.hash(rules);
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
    DefaultRuleSet other = (DefaultRuleSet) obj;
    return Objects.equals(rules, other.rules);
  }

  @Override
  public String toString() {
    return "DefaultRuleSet [rules=" + rules + "]";
  }
}
