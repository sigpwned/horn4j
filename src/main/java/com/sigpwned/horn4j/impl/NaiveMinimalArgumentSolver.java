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

import static java.util.Collections.unmodifiableSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.horn4j.AbductiveClosure;
import com.sigpwned.horn4j.AbductiveReasoner;
import com.sigpwned.horn4j.Argument;
import com.sigpwned.horn4j.DeductiveClosure;
import com.sigpwned.horn4j.DeductiveReasoner;
import com.sigpwned.horn4j.MinimalArgumentSolver;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.util.PruningPowerSetWalker;
import com.sigpwned.horn4j.util.Sets;

public class NaiveMinimalArgumentSolver<RuleIdT, PropositionT>
    implements MinimalArgumentSolver<RuleIdT, PropositionT> {

  @Override
  public Set<Argument<RuleIdT, PropositionT>> minimalArguments(RuleSet<RuleIdT, PropositionT> rules,
      PropositionT hypothesis) {
    return minimalArguments(rules, Set.of(hypothesis));
  }

  @Override
  public Set<Argument<RuleIdT, PropositionT>> minimalArguments(RuleSet<RuleIdT, PropositionT> rules,
      Set<PropositionT> hypotheses) {
    if (rules == null)
      throw new NullPointerException();
    if (hypotheses == null)
      throw new NullPointerException();

    // We'll be doing some abduction, so get ready
    final AbductiveReasoner<RuleIdT, PropositionT> ar = new NaiveAbductiveReasoner<>();

    // Compute the abductive closure of our hypothesis. This provides a basis for the hypotheses
    // that is sufficient, but not necessarily minimal.
    final AbductiveClosure<RuleIdT, PropositionT> ac =
        new NaiveAbductiveReasoner<RuleIdT, PropositionT>().abduct(hypotheses, rules);

    // If the hypotheses are all postulates, then the minimal argument is just the hypotheses
    // themselves with no rules.
    if (ac.getPostulates().containsAll(hypotheses)) {
      return Set.of(new Argument<>(hypotheses, Set.of()));
    }

    // If the hypotheses are not all postulates, then the hypotheses must be entailed by the
    // postulates and lemmas.
    assert Sets.union(ac.getPostulates(), ac.getLemmas()).containsAll(hypotheses);

    // We'll be doing some deduction, so get ready
    final DeductiveReasoner<RuleIdT, PropositionT> dr = new NaiveDeductiveReasoner<>();

    // We'll determine the minimal arguments by testing all subsets of the postulates and
    // seeing if they entail the hypotheses. Once we've found a subset of the postulates that
    // entails the hypotheses, we don't need to visit any supersets of that subset, since any such
    // supersets would necessarily not be minimal. This allows us to prune the search space
    // substantially, which makes this approach practical. This walker implements that pruning.
    final Set<Argument<RuleIdT, PropositionT>> result = new LinkedHashSet<>();
    new PruningPowerSetWalker<PropositionT>().prunedWalk(ac.getPostulates(), (subset) -> {
      // NOTE: The walker is guaranteed to visit subsets in order of increasing size.

      // Compute the deductive closure of this subset of postulates. We got here, so we know that
      // no proper subset of this subset of postulates entailed the hypotheses. Therefore, if this
      // subset of postulates entails the hypotheses, then it is a minimal argument.
      final DeductiveClosure<RuleIdT, PropositionT> dc = dr.deduct(subset, ac.getRules());
      if (!dc.getConclusions().containsAll(hypotheses)) {
        // This subset of postulates does not entail the hypotheses. Therefore, it is not a minimal
        // argument, and we should continue searching.
        return false;
      }

      // We have the minimal set of postulates that entail the hypotheses. Now we need to find the
      // minimal set of rules that are necessary and sufficient to derive the hypotheses from the
      // postulates. We do this by abducting the hypotheses from the postulates and the subset of
      // rules that were fired during the deduction. This will provide us with a basis for the
      // hypotheses that is both necessary and sufficient, and therefore minimal.
      final AbductiveClosure<RuleIdT, PropositionT> dcac =
          ar.abduct(hypotheses, new DefaultRuleSet<>(List.copyOf(dc.getFired())));

      // Hot damn! Store that minimal argument!
      result.add(new Argument<>(Set.copyOf(subset), dcac.getFired()));

      return true;
    });

    return unmodifiableSet(result);
  }
}
