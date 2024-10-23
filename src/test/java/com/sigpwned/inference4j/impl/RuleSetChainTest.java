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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;

public class RuleSetChainTest {
  @Test
  public void givenConsequent_whenFindByConsequent_thenReturnMatchingRules() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSet<String, String> ruleSet2 = new DefaultRuleSet<>(
        List.of(new Rule<>("2", Set.of("c"), "d"), new Rule<>("3", Set.of("d"), "e")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);
    chain.addLast(ruleSet2);

    Set<Rule<String, String>> result = chain.findByConsequent("c");

    assertEquals(Set.of(new Rule<>("1", Set.of("b"), "c")), result);
  }

  @Test
  public void givenSignature_whenFindBySignature_thenReturnMatchingRules() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSet<String, String> ruleSet2 = new DefaultRuleSet<>(
        List.of(new Rule<>("2", Set.of("c"), "d"), new Rule<>("3", Set.of("d"), "e")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);
    chain.addLast(ruleSet2);

    Set<Rule<String, String>> result = chain.findBySignature(Set.of("b"), "c");

    assertEquals(Set.of(new Rule<>("1", Set.of("b"), "c")), result);
  }

  @Test
  public void givenExactAntecedents_whenFindByExactAntecedents_thenReturnMatchingRules() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSet<String, String> ruleSet2 = new DefaultRuleSet<>(
        List.of(new Rule<>("2", Set.of("c"), "d"), new Rule<>("3", Set.of("d"), "e")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);
    chain.addLast(ruleSet2);

    Set<Rule<String, String>> result = chain.findByExactAntecedents(Set.of("a"));

    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b")), result);
  }

  @Test
  public void givenSatisfiedAntecedents_whenFindBySatisfiedAntecedents_thenReturnMatchingRules() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSet<String, String> ruleSet2 = new DefaultRuleSet<>(
        List.of(new Rule<>("2", Set.of("c"), "d"), new Rule<>("3", Set.of("d"), "e")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);
    chain.addLast(ruleSet2);

    Set<Rule<String, String>> result = chain.findBySatisfiedAntecedents(Set.of("a", "b"));

    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")),
        result);
  }

  @Test
  public void givenNonMatchingConsequent_whenFindByConsequent_thenReturnEmptySet() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);

    Set<Rule<String, String>> result = chain.findByConsequent("x");

    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingSignature_whenFindBySignature_thenReturnEmptySet() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);

    Set<Rule<String, String>> result = chain.findBySignature(Set.of("x"), "y");

    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingExactAntecedents_whenFindByExactAntecedents_thenReturnEmptySet() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);

    Set<Rule<String, String>> result = chain.findByExactAntecedents(Set.of("x"));

    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingSatisfiedAntecedents_whenFindBySatisfiedAntecedents_thenReturnEmptySet() {
    RuleSet<String, String> ruleSet1 = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    RuleSetChain<String, String> chain = new RuleSetChain<>();
    chain.addLast(ruleSet1);

    Set<Rule<String, String>> result = chain.findBySatisfiedAntecedents(Set.of("x", "y"));

    assertTrue(result.isEmpty());
  }
}
