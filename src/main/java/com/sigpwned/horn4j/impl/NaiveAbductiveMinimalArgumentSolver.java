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
import java.util.HashSet;
import java.util.Set;
import com.sigpwned.horn4j.AbductiveMinimalArgumentSolver;
import com.sigpwned.horn4j.AbductiveWalk;
import com.sigpwned.horn4j.AbductiveWalker;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.util.Sets;

public class NaiveAbductiveMinimalArgumentSolver<RuleIdT, PropositionT>
    implements AbductiveMinimalArgumentSolver<RuleIdT, PropositionT> {
  private final AbductiveWalker<RuleIdT, PropositionT> walker;

  public NaiveAbductiveMinimalArgumentSolver() {
    this(new NaiveAbductiveWalker<>());
  }

  public NaiveAbductiveMinimalArgumentSolver(AbductiveWalker<RuleIdT, PropositionT> walker) {
    this.walker = requireNonNull(walker);
  }

  @Override
  public Set<AbductiveWalk<RuleIdT, PropositionT>> solve(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> ruleset, Set<PropositionT> goals) {
    final Set<AbductiveWalk<RuleIdT, PropositionT>> solutions = new HashSet<>();

    getWalker().walk(assumptions, ruleset, (walk) -> {
      if (walk.getEvidence().containsAll(goals)) {
        // Once we see a solution, we can prune the search space. There's no need to keep going.
        solutions.add(walk);
        return AbductiveWalker.Instruction.PRUNE;
      }
      return AbductiveWalker.Instruction.CONTINUE;
    });

    // If there are no solutions, then return an empty set.
    if (solutions.isEmpty()) {
      return Sets.of();
    }

    // If there is only one solution, then it is minimal by definition, since there is no other
    // solution that could be smaller.
    if (solutions.size() == 1) {
      AbductiveWalk<RuleIdT, PropositionT> solution = solutions.iterator().next();
      return Sets.of(solution);
    }

    // Otherwise, we need to filter out any solutions that are subsumed by other solutions.
    final Set<AbductiveWalk<RuleIdT, PropositionT>> minimalSolutions = solutions.stream()
        .filter(s1 -> solutions.stream().noneMatch(s2 -> s2 != s1 && s1.contains(s2)))
        .collect(toSet());

    return unmodifiableSet(minimalSolutions);
  }

  /**
   * @return the walker
   */
  private AbductiveWalker<RuleIdT, PropositionT> getWalker() {
    return walker;
  }
}
