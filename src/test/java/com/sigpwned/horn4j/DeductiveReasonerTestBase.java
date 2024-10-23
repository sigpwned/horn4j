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

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.horn4j.DeductiveClosure;
import com.sigpwned.horn4j.DeductiveReasoner;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.impl.DefaultRuleSet;

public abstract class DeductiveReasonerTestBase {
  public abstract DeductiveReasoner<String, String> newDeductiveReasoner();

  @Test
  public void givenEmptyAssumptions_whenDeduct_thenEmptyClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of();
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet, emptySet(), emptySet()), result);
  }

  @Test
  public void givenSingleAssumption_whenDeduct_thenSingleClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet, Set.copyOf(rules), Set.of("b")),
        result);
  }

  @Test
  public void givenMultipleAssumptions_whenDeduct_thenMultipleClosures() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a");
    List<Rule<String, String>> rules =
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet, Set.copyOf(rules), Set.of("b", "c")),
        result);
  }

  @Test
  public void givenNoMatchingRules_whenDeduct_thenNoClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet, emptySet(), emptySet()), result);
  }

  @Test
  public void givenMultipleAssumptionsAndRules_whenDeduct_thenCorrectClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a", "d");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"),
        new Rule<>("1", Set.of("b"), "c"), new Rule<>("2", Set.of("d"), "e"),
        new Rule<>("3", Set.of("e"), "f"), new Rule<>("4", Set.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet,
        Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"),
            new Rule<>("2", Set.of("d"), "e"), new Rule<>("3", Set.of("e"), "f")),
        Set.of("b", "c", "e", "f")), result);
  }

  @Test
  public void givenMultipleAssumptionsAndPartialMatchingRules_whenDeduct_thenPartialClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a", "d");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"),
        new Rule<>("1", Set.of("b"), "c"), new Rule<>("2", Set.of("d"), "e"),
        new Rule<>("3", Set.of("e"), "f"), new Rule<>("4", Set.of("b", "d"), "g"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet,
        Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"),
            new Rule<>("2", Set.of("d"), "e"), new Rule<>("3", Set.of("e"), "f"),
            new Rule<>("4", Set.of("b", "d"), "g")),
        Set.of("b", "c", "e", "f", "g")), result);
  }

  @Test
  public void givenMultipleAssumptionsAndNonMatchingRules_whenDeduct_thenNoAdditionalClosure() {
    DeductiveReasoner<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Set.of("a", "d");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("x"), "y"),
        new Rule<>("1", Set.of("y"), "z"), new Rule<>("2", Set.of("z"), "w"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveClosure<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveClosure<>(assumptions, ruleSet, emptySet(), emptySet()), result);
  }
}
