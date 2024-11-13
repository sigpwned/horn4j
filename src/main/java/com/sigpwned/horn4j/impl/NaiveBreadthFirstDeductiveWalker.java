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

import java.util.HashSet;
import java.util.Set;
import com.sigpwned.horn4j.DeductiveWalk;
import com.sigpwned.horn4j.DeductiveWalker;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.util.Sets;

/**
 * Walks the given rules deductively in <em>breadth-first</em> order.
 * 
 * @param <RuleIdT> the type of the rule identifiers
 * @param <PropositionT> the type of the propositions
 */
public class NaiveBreadthFirstDeductiveWalker<RuleIdT, PropositionT>
    implements DeductiveWalker<RuleIdT, PropositionT> {
  @Override
  public void walk(Set<PropositionT> assumptions, RuleSet<RuleIdT, PropositionT> ruleset,
      Visitor<RuleIdT, PropositionT> visitor) {
    Set<DeductiveWalk<RuleIdT, PropositionT>> additions = new HashSet<>();
    additions.add(DeductiveWalk.start(assumptions));
    do {
      Set<DeductiveWalk<RuleIdT, PropositionT>> newAdditions = null;

      for (DeductiveWalk<RuleIdT, PropositionT> walk : additions) {
        Instruction instruction = visitor.step(walk);
        switch (instruction) {
          case CONTINUE:
            // Carry on with this walk. Do nothing.
            break;
          case PRUNE:
            // Stop this walk only. Do not take any new steps on this walk.
            continue;
          case STOP:
            // Stop all walks. Do not take any new steps on any walk.
            return;
        }

        Set<Rule<RuleIdT, PropositionT>> fireableRules = ruleset
            .findBySatisfiedAntecedents(Sets.union(walk.getAssumptions(), walk.getConclusions()));

        for (Rule<RuleIdT, PropositionT> rule : fireableRules) {
          if (walk.getAssumptions().contains(rule.getConsequent())) {
            // We're assuming the consequent of this rule is true, so we don't need to prove it.
            // Skip this rule.
            continue;
          }
          if (walk.getConclusions().contains(rule.getConsequent())) {
            // We've already proven the consequent of this rule is true. Skip this rule.
            continue;
          }
          if (walk.getFired().contains(rule)) {
            // We've already fired this rule. Skip this rule.
            continue;
          }

          if (newAdditions == null) {
            newAdditions = new HashSet<>();
          }

          newAdditions.add(walk.step(rule));
        }
      }

      additions = newAdditions;
    } while (additions != null);
  }
}
