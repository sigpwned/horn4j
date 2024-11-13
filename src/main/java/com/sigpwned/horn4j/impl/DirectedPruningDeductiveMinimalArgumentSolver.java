/*-
 * =================================LICENSE_START==================================
 * horn4j
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

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.horn4j.DeductiveMinimalArgumentSolver;
import com.sigpwned.horn4j.DeductiveWalk;
import com.sigpwned.horn4j.DeductiveWalker;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.util.Lists;
import com.sigpwned.horn4j.util.Sets;

/**
 * This solver performs a {@link DeductiveWalker deductive walk} over a {@link RuleSet rule set}
 * from an initial set of {@link PropositionT assumptions} to a set of {@link PropositionT goals}.
 * 
 * <p>
 * At each step in the walk, for each goal that is still unsatisfied at that point in the walk, it
 * searches for a single eligible rule that can satisfy that goal. If it can find such a rule for
 * each unsatisfied goal, then it applies all of those rules to the current walk, and records that
 * as a solution and prunes the search space, since any other solution would have to be longer, and
 * therefore not minimal. Otherwise, it continues the walk.
 * 
 * <p>
 * This results in a (minimally) directed walk through the search space, which is more efficient
 * than a completely naive, undirected walk. It still identifies all minimal solutions, per the
 * contract for {@link DeductiveMinimalArgumentSolver}, but does so more efficiently.
 * 
 * @param <RuleIdT> The type of the rule identifiers.
 * @param <PropositionT> The type of the propositions.
 */
public class DirectedPruningDeductiveMinimalArgumentSolver<RuleIdT, PropositionT>
    implements DeductiveMinimalArgumentSolver<RuleIdT, PropositionT> {
  private final DeductiveWalker<RuleIdT, PropositionT> walker;

  public DirectedPruningDeductiveMinimalArgumentSolver() {
    this(new NaiveBreadthFirstDeductiveWalker<>());
  }

  public DirectedPruningDeductiveMinimalArgumentSolver(
      DeductiveWalker<RuleIdT, PropositionT> walker) {
    this.walker = requireNonNull(walker);
  }

  @Override
  public Set<DeductiveWalk<RuleIdT, PropositionT>> solve(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> ruleset, Set<PropositionT> goals) {
    final Set<DeductiveWalk<RuleIdT, PropositionT>> solutions = new HashSet<>();

    getWalker().walk(assumptions, ruleset, (walk) -> {
      // If we're done, we're done. Prune.
      if (walk.getConclusions().containsAll(goals)) {
        // Once we have a solution, we can prune the search space. There's no need to keep going.
        solutions.add(walk);
        return DeductiveWalker.Instruction.PRUNE;
      }

      // Otherwise, if we're not done, can we easily get to done from here? Try to satisfy each
      // missing goal (of which there is at least one, per the above) with a single rule. If we can,
      // then we can prune the search space. If we can't, then we need to keep going. While this
      // looks like a lot of work, this is at least a directed walk, which is better than the
      // undirected walk that we get otherwise. Also, each prune here is a significant reduction in
      // the search space.
      Set<PropositionT> satisfied = Sets.union(walk.getAssumptions(), walk.getConclusions());
      List<PropositionT> unsatisfiedGoals =
          new ArrayList<>(Sets.difference(goals, walk.getConclusions()));
      List<Set<Rule<RuleIdT, PropositionT>>> unsatisfiedGoalsCandidates = new ArrayList<>();
      for (int i = 0; i < unsatisfiedGoals.size(); i++) {
        // This is the goal we're trying to satisfy.
        PropositionT unsatisfiedGoal = unsatisfiedGoals.get(i);

        // Find all individual eligible rules that can satisfy this goal with the available
        // satisfied propositions.
        Set<Rule<RuleIdT, PropositionT>> unsatisfiedGoalCandidates =
            ruleset.findByConsequent(unsatisfiedGoal).stream()
                .filter(r -> satisfied.containsAll(r.getAntecedents()))
                .filter(r -> !walk.getFired().contains(r)).collect(toSet());

        // Is there at least one rule that can satisfy this goal?
        if (unsatisfiedGoalCandidates.isEmpty()) {
          // If we can't satisfy any of our goals with just one rule, then we just bail out and
          // continue the search.
          return DeductiveWalker.Instruction.CONTINUE;
        }

        // Otherwise, we have at least one rule that can satisfy this goal. We need to keep track of
        // all of the rules that can satisfy this goal, so that we can try all combinations of rules
        // that can satisfy all of the goals.
        unsatisfiedGoalsCandidates.add(unsatisfiedGoalCandidates);
      }

      // Every combination of rules that can satisfy all of the goals is a potential solution.
      List<Set<Rule<RuleIdT, PropositionT>>> product =
          Lists.cartesianProduct(unsatisfiedGoalsCandidates);

      // For each combination of rules that can satisfy all of the goals, we need to apply all of
      // those rules to the current walk and record it as a solution.
      for (Set<Rule<RuleIdT, PropositionT>> rules : product) {
        DeductiveWalk<RuleIdT, PropositionT> solution = walk;
        for (Rule<RuleIdT, PropositionT> rule : rules) {
          solution = solution.step(rule);
        }
        solutions.add(solution);
      }

      // Prune the space!
      return DeductiveWalker.Instruction.PRUNE;
    });

    // If there are no solutions, then return an empty set.
    if (solutions.isEmpty()) {
      return Sets.of();
    }

    // If there is only one solution, then it is minimal by definition, since there is no other
    // solution that could be smaller.
    if (solutions.size() == 1) {
      DeductiveWalk<RuleIdT, PropositionT> solution = solutions.iterator().next();
      return Sets.of(solution);
    }

    // Otherwise, we need to filter out any solutions that are subsumed by other solutions.
    final Set<DeductiveWalk<RuleIdT, PropositionT>> minimalSolutions = solutions.stream()
        .filter(s1 -> solutions.stream().noneMatch(s2 -> s2 != s1 && s1.contains(s2)))
        .collect(toSet());

    return unmodifiableSet(minimalSolutions);
  }

  private DeductiveWalker<RuleIdT, PropositionT> getWalker() {
    return walker;
  }
}
