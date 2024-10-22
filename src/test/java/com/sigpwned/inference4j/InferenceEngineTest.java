package com.sigpwned.inference4j;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.inference4j.impl.DefaultProductionSet;

public class InferenceEngineTest {
  @Test
  public void deductTest1() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("a"), "b")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<String> satisfied = new HashSet<>();
    List<Rule<String, String>> productions = new ArrayList<>();
    engine.deduct(Set.of("a"), new DeductionListener<>() {
      @Override
      public void deduction(Rule<String, String> production) {
        productions.add(production);
        satisfied.add(production.getConsequent());
      }
    });

    assertEquals(List.of(new Rule<>("0", Set.of("a"), "b")), productions);
    assertEquals(Set.of("b"), satisfied);
  }

  @Test
  public void deductTest2() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("a"), "b")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<String> satisfied = new HashSet<>();
    List<Rule<String, String>> productions = new ArrayList<>();
    engine.deduct(Set.of(), new DeductionListener<>() {
      @Override
      public void deduction(Rule<String, String> production) {
        productions.add(production);
        satisfied.add(production.getConsequent());
      }
    });

    assertEquals(List.of(), productions);
    assertEquals(Set.of(), satisfied);
  }

  @Test
  public void deductTest3() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("a"), "c"),
            new Rule<>("3", Set.of("b"), "d"), new Rule<>("4", Set.of("c"), "e"),
            new Rule<>("5", Set.of("g"), "h")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<String> satisfied = new HashSet<>();
    List<Rule<String, String>> productions = new ArrayList<>();
    engine.deduct(Set.of("a", "i"), new DeductionListener<>() {
      @Override
      public void deduction(Rule<String, String> production) {
        productions.add(production);
        satisfied.add(production.getConsequent());
      }
    });

    assertEquals(List.of(new Rule<>("1", Set.of("a"), "b"),
        new Rule<>("2", Set.of("a"), "c"), new Rule<>("3", Set.of("b"), "d"),
        new Rule<>("4", Set.of("c"), "e"), new Rule<>("0", Set.of("d", "e"), "f")),
        productions);
    assertEquals(Set.of("b", "c", "d", "e", "f"), satisfied);
  }

  @Test
  public void givenSeveralRules_whenNecessaryRequiringDirectReasoning_thenReturnCorrectAntecedents() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("a"), "c"),
            new Rule<>("3", Set.of("b"), "d"), new Rule<>("4", Set.of("c"), "e"),
            new Rule<>("5", Set.of("g"), "h")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<Set<String>> necessary = engine.necessary("b");

    assertEquals(Set.of(Set.of("a")), necessary);
  }

  @Test
  public void givenSeveralRules_whenNecessaryRequiringTransitiveReasoning_thenReturnCorrectAntecedents() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("a"), "c"),
            new Rule<>("3", Set.of("b"), "d"), new Rule<>("4", Set.of("c"), "e"),
            new Rule<>("5", Set.of("g"), "h")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<Set<String>> necessary = engine.necessary("f");

    assertEquals(Set.of(Set.of("a")), necessary);
  }

  @Test
  public void givenSeveralRules_whenNecessaryRequiringMultipleAntecedents_thenReturnCorrectAntecedents() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("c"), "d"),
            new Rule<>("5", Set.of("g"), "h")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<Set<String>> necessary = engine.necessary("f");

    assertEquals(Set.of(Set.of("c", "e")), necessary);
  }

  @Test
  public void givenSeveralRules_whenNecessaryWithMultipleAlternatives_thenReturnCorrectAntecedents() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("c"), "d"),
            new Rule<>("5", Set.of("g"), "h"), new Rule<>("6", Set.of("x"), "e"),
            new Rule<>("7", Set.of("y", "z"), "e")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<Set<String>> necessary = engine.necessary("f");

    assertEquals(Set.of(Set.of("c", "x"), Set.of("c", "y", "z")), necessary);
  }

  @Test
  public void givenSeveralRules_whenMultiNecessaryWithMultipleAlternatives_thenReturnCorrectAntecedents() {
    RuleSet<String, String> ps =
        new DefaultProductionSet<>(List.of(new Rule<>("0", Set.of("d", "e"), "f"),
            new Rule<>("1", Set.of("a"), "b"), new Rule<>("2", Set.of("c"), "d"),
            new Rule<>("5", Set.of("g"), "h"), new Rule<>("6", Set.of("x"), "e"),
            new Rule<>("7", Set.of("y", "z"), "e")));

    InferenceEngine<String, String> engine = new InferenceEngine<>(ps);

    Set<Set<String>> necessary = engine.necessary(Set.of("f", "y"));

    assertEquals(Set.of(Set.of("c", "x", "y"), Set.of("c", "y", "z")), necessary);
  }
}
