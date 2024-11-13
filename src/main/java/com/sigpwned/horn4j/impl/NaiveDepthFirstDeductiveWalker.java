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

import java.util.Set;
import java.util.Stack;
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
public class NaiveDepthFirstDeductiveWalker<RuleIdT, PropositionT>
    implements DeductiveWalker<RuleIdT, PropositionT> {

  @Override
  public void walk(Set<PropositionT> assumptions, RuleSet<RuleIdT, PropositionT> ruleset,
      Visitor<RuleIdT, PropositionT> visitor) {

    // Use a stack to manage the DFS
    Stack<DeductiveWalk<RuleIdT, PropositionT>> stack = new Stack<>();
    stack.push(DeductiveWalk.start(assumptions));

    while (!stack.isEmpty()) {
      DeductiveWalk<RuleIdT, PropositionT> currentWalk = stack.pop();

      // Visit the current state of this walk
      Instruction instruction = visitor.step(currentWalk);
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

      // Find all rules that can fire given the current assumptions and conclusions
      Set<Rule<RuleIdT, PropositionT>> fireableRules = ruleset.findBySatisfiedAntecedents(
          Sets.union(currentWalk.getAssumptions(), currentWalk.getConclusions()));

      for (Rule<RuleIdT, PropositionT> rule : fireableRules) {
        if (currentWalk.getAssumptions().contains(rule.getConsequent())) {
          continue; // Skip as the consequent is already assumed
        }
        if (currentWalk.getConclusions().contains(rule.getConsequent())) {
          continue; // Skip as the consequent is already concluded
        }
        if (currentWalk.getFired().contains(rule)) {
          continue; // Skip as the rule has already been fired
        }

        // Push a new walk to the stack to dive deeper into this path
        stack.push(currentWalk.step(rule));
      }
    }
  }
}
