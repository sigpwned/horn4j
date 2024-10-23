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

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.inference4j.impl.DefaultRuleSet;

public abstract class MinimalArgumentReasonerTestBase {
  public abstract MinimalArgumentReasoner<String, String> newMinimalArgumentReasoner();

  @Test
  public void givenEmptyHypotheses_whenMinimalArguments_thenTrivialSolution() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of();
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(Set.of(new Argument<>(hypotheses, emptySet())), result);
  }

  @Test
  public void givenSingleHypothesis_whenMinimalArguments_thenSingleArgument() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("b");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(Set.of(new Argument<>(Set.of("a"), Set.of(new Rule<>("0", Set.of("a"), "b")))),
        result);
  }

  @Test
  public void givenMultipleHypotheses_whenMinimalArguments_thenMultipleArguments() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("c");
    List<Rule<String, String>> rules =
        List.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(Set.of(new Argument<>(Set.of("a"),
        Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c")))), result);
  }

  @Test
  public void givenNoMatchingRules_whenMinimalArguments_thenTrivialSolution() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("a");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(Set.of(new Argument<>(hypotheses, emptySet())), result);
  }

  @Test
  public void givenMultipleHypothesesAndRules_whenMinimalArguments_thenCorrectArguments() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("c", "f");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"),
        new Rule<>("1", Set.of("b"), "c"), new Rule<>("2", Set.of("d"), "e"),
        new Rule<>("3", Set.of("e"), "f"), new Rule<>("4", Set.of("x"), "y"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(
        Set.of(
            new Argument<>(Set.of("a", "d"),
                Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"),
                    new Rule<>("2", Set.of("d"), "e"), new Rule<>("3", Set.of("e"), "f")))),
        result);
  }

  @Test
  public void givenMultipleHypothesesAndPartialMatchingRules_whenMinimalArguments_thenPartialArguments() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("c", "f");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("a"), "b"),
        new Rule<>("1", Set.of("b"), "c"), new Rule<>("2", Set.of("d"), "e"),
        new Rule<>("3", Set.of("e"), "f"), new Rule<>("4", Set.of("b", "d"), "g"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(
        Set.of(
            new Argument<>(Set.of("a", "d"),
                Set.of(new Rule<>("0", Set.of("a"), "b"), new Rule<>("1", Set.of("b"), "c"),
                    new Rule<>("2", Set.of("d"), "e"), new Rule<>("3", Set.of("e"), "f")))),
        result);
  }

  @Test
  public void givenMultipleHypothesesAndNonMatchingRules_whenMinimalArguments_thenTrivialSolution() {
    MinimalArgumentReasoner<String, String> reasoner = newMinimalArgumentReasoner();
    Set<String> hypotheses = Set.of("a", "d");
    List<Rule<String, String>> rules = List.of(new Rule<>("0", Set.of("x"), "y"),
        new Rule<>("1", Set.of("y"), "z"), new Rule<>("2", Set.of("z"), "w"));
    RuleSet<String, String> ruleSet = new DefaultRuleSet<>(rules);
    Set<Argument<String, String>> result = reasoner.minimalArguments(ruleSet, hypotheses);
    assertEquals(Set.of(new Argument<>(hypotheses, emptySet())), result);
  }
}
