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

import static org.junit.Assert.assertEquals;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.horn4j.impl.DefaultRuleSet;
import com.sigpwned.horn4j.util.Sets;

public abstract class DeductiveReasonerTestBase {
  public abstract DeductiveClosureSolver<String, String> newDeductiveReasoner();

  @Test
  public void givenEmptyAssumptions_whenDeduct_thenEmptyClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of();
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions), result);
  }

  @Test
  public void givenSingleAssumption_whenDeduct_thenSingleClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions, Sets.newLinkedHashSet(rules), Sets.of("b")),
        result);
  }

  @Test
  public void givenMultipleAssumptions_whenDeduct_thenMultipleClosures() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a");
    Set<Rule<String, String>> rules =
        Sets.of(new Rule<>("0", Sets.of("a"), "b"), new Rule<>("1", Sets.of("b"), "c"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions, Sets.newLinkedHashSet(rules), Sets.of("b", "c")),
        result);
  }

  @Test
  public void givenNoMatchingRules_whenDeduct_thenNoClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions), result);
  }

  @Test
  public void givenMultipleAssumptionsAndRules_whenDeduct_thenCorrectClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a", "d");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"),
        new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
        new Rule<>("3", Sets.of("e"), "f"), new Rule<>("4", Sets.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions,
        Sets.newLinkedHashSet(new Rule<>("0", Sets.of("a"), "b"),
            new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
            new Rule<>("3", Sets.of("e"), "f")),
        Sets.of("b", "c", "e", "f")), result);
  }

  @Test
  public void givenMultipleAssumptionsAndPartialMatchingRules_whenDeduct_thenPartialClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a", "d");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"),
        new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
        new Rule<>("3", Sets.of("e"), "f"), new Rule<>("4", Sets.of("b", "d"), "g"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions,
        Sets.newLinkedHashSet(new Rule<>("0", Sets.of("a"), "b"),
            new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
            new Rule<>("3", Sets.of("e"), "f"), new Rule<>("4", Sets.of("b", "d"), "g")),
        Sets.of("b", "c", "e", "f", "g")), result);
  }

  @Test
  public void givenMultipleAssumptionsAndNonMatchingRules_whenDeduct_thenNoAdditionalClosure() {
    DeductiveClosureSolver<String, String> reasoner = newDeductiveReasoner();
    Set<String> assumptions = Sets.of("a", "d");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("x"), "y"),
        new Rule<>("1", Sets.of("y"), "z"), new Rule<>("2", Sets.of("z"), "w"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    DeductiveWalk<String, String> result = reasoner.deduct(assumptions, ruleSet);
    assertEquals(new DeductiveWalk<>(assumptions), result);
  }
}
