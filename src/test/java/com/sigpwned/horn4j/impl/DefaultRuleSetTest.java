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
package com.sigpwned.horn4j.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.impl.DefaultRuleSet;

public class DefaultRuleSetTest {
  @Test
  public void givenEmptyRuleSet_whenFindByConsequent_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(List.of());
    Set<Rule<String, String>> result = ruleSet.findByConsequent("a");
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenEmptyRuleSet_whenFindByExactAntecedents_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(List.of());
    Set<Rule<String, String>> result = ruleSet.findByExactAntecedents(Set.of("a"));
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenEmptyRuleSet_whenFindBySatisfiedAntecedents_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(List.of());
    Set<Rule<String, String>> result = ruleSet.findBySatisfiedAntecedents(Set.of("a"));
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenEmptyRuleSet_whenFindBySignature_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(List.of());
    Set<Rule<String, String>> result = ruleSet.findBySignature(Set.of("a"), "b");
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonEmptyRuleSet_whenFindByConsequent_thenReturnMatchingRules() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findByConsequent("b");
    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b")), result);
  }

  @Test
  public void givenNonEmptyRuleSet_whenFindByExactAntecedents_thenReturnMatchingRules() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findByExactAntecedents(Set.of("a"));
    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b")), result);
  }

  @Test
  public void givenNonEmptyRuleSet_whenFindBySatisfiedAntecedents_thenReturnMatchingRules() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findBySatisfiedAntecedents(Set.of("a", "b"));
    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")),
        result);
  }

  @Test
  public void givenNonEmptyRuleSet_whenFindBySignature_thenReturnMatchingRules() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findBySignature(Set.of("a"), "b");
    assertEquals(Set.of(new Rule<>("0", Set.of("a"), "b")), result);
  }

  @Test
  public void givenNonMatchingConsequent_whenFindByConsequent_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findByConsequent("x");
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingExactAntecedents_whenFindByExactAntecedents_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findByExactAntecedents(Set.of("x"));
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingSatisfiedAntecedents_whenFindBySatisfiedAntecedents_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findBySatisfiedAntecedents(Set.of("x", "y"));
    assertTrue(result.isEmpty());
  }

  @Test
  public void givenNonMatchingSignature_whenFindBySignature_thenReturnEmptySet() {
    DefaultRuleSet<String, String> ruleSet = new DefaultRuleSet<>(
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")));
    Set<Rule<String, String>> result = ruleSet.findBySignature(Set.of("x"), "y");
    assertTrue(result.isEmpty());
  }
}
