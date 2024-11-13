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
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.horn4j.impl.DefaultRuleSet;
import com.sigpwned.horn4j.util.Sets;

public abstract class AbductiveReasonerTestBase {
  public abstract AbductiveClosureSolver<String, String> newAbductiveReasoner();

  @Test
  public void givenEmptyHypotheses_whenAbduct_thenEmptyClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of();
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);
    assertEquals(new AbductiveWalk<>(hypotheses), result);
  }

  @Test
  public void givenSingleHypothesis_whenAbduct_thenSingleClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("b");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);
    assertEquals(new AbductiveWalk<>(hypotheses, Sets.newLinkedHashSet(rules), Sets.of("a")),
        result);
  }

  @Test
  public void givenMultipleHypotheses_whenAbduct_thenMultipleClosures() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("c");
    Set<Rule<String, String>> rules =
        Sets.of(new Rule<>("0", Sets.of("a"), "b"), new Rule<>("1", Sets.of("b"), "c"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);
    assertEquals(new AbductiveWalk<>(hypotheses, Sets.newLinkedHashSet(rules), Sets.of("a", "b")),
        result);
  }

  @Test
  public void givenNoMatchingRules_whenAbduct_thenNoClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("a");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);
    assertEquals(new AbductiveWalk<>(hypotheses, Sets.newLinkedHashSet(), Sets.of("a")), result);
  }

  @Test
  public void givenMultipleHypothesesAndRules_whenAbduct_thenCorrectClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("c", "f");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"),
        new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
        new Rule<>("3", Sets.of("e"), "f"), new Rule<>("4", Sets.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);

    LinkedHashSet<Rule<String, String>> expectedFired = new LinkedHashSet<>();
    expectedFired.add(new Rule<>("0", Sets.of("a"), "b"));
    expectedFired.add(new Rule<>("1", Sets.of("b"), "c"));
    expectedFired.add(new Rule<>("2", Sets.of("d"), "e"));
    expectedFired.add(new Rule<>("3", Sets.of("e"), "f"));

    assertEquals(new AbductiveWalk<>(hypotheses, expectedFired, Sets.of("a", "d", "b", "e")),
        result);
  }

  @Test
  public void givenMultipleHypothesesAndPartialMatchingRules_whenAbduct_thenPartialClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("c", "f");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("a"), "b"),
        new Rule<>("1", Sets.of("b"), "c"), new Rule<>("2", Sets.of("d"), "e"),
        new Rule<>("3", Sets.of("e"), "f"), new Rule<>("4", Sets.of("b", "d"), "g"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);

    LinkedHashSet<Rule<String, String>> expectedFired = new LinkedHashSet<>();
    expectedFired.add(new Rule<>("0", Sets.of("a"), "b"));
    expectedFired.add(new Rule<>("1", Sets.of("b"), "c"));
    expectedFired.add(new Rule<>("2", Sets.of("d"), "e"));
    expectedFired.add(new Rule<>("3", Sets.of("e"), "f"));

    assertEquals(new AbductiveWalk<>(hypotheses, expectedFired, Sets.of("a", "d", "b", "e")),
        result);
  }

  @Test
  public void givenMultipleHypothesesAndNonMatchingRules_whenAbduct_thenNoClosure() {
    AbductiveClosureSolver<String, String> reasoner = newAbductiveReasoner();
    Set<String> hypotheses = Sets.of("a", "d");
    Set<Rule<String, String>> rules = Sets.of(new Rule<>("0", Sets.of("x"), "y"),
        new Rule<>("1", Sets.of("y"), "z"), new Rule<>("2", Sets.of("z"), "w"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    AbductiveWalk<String, String> result = reasoner.abduct(hypotheses, ruleSet);
    assertEquals(new AbductiveWalk<>(hypotheses, Sets.newLinkedHashSet(), Sets.of("a", "d")),
        result);
  }
}
