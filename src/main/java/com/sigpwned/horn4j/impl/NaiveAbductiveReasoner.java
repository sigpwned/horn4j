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

import static java.util.Collections.emptySet;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import com.sigpwned.horn4j.AbductiveClosure;
import com.sigpwned.horn4j.AbductiveReasoner;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;

public class NaiveAbductiveReasoner<RuleIdT, PropositionT>
    implements AbductiveReasoner<RuleIdT, PropositionT> {

  @Override
  public AbductiveClosure<RuleIdT, PropositionT> abduct(Set<PropositionT> hypotheses,
      RuleSet<RuleIdT, PropositionT> rules) {
    if (hypotheses == null)
      throw new NullPointerException();
    if (rules == null)
      throw new NullPointerException();

    if (hypotheses.isEmpty())
      return new AbductiveClosure<>(hypotheses, rules, emptySet(), emptySet(), emptySet());

    Set<PropositionT> postulates = new HashSet<>();
    Set<PropositionT> lemmas = new HashSet<>();
    Set<Rule<RuleIdT, PropositionT>> fired = new HashSet<>();

    Queue<PropositionT> queue = new ArrayDeque<>(hypotheses);
    Set<PropositionT> queued = new HashSet<>(hypotheses);
    do {
      PropositionT next = queue.poll();

      // TODO Should we do some kind of session to make calculation in RuleSet more efficient?
      Set<Rule<RuleIdT, PropositionT>> abducteds = rules.findByConsequent(next);
      if (abducteds.isEmpty()) {
        // Since no rules infer us, we must be a postulate.
        postulates.add(next);
      } else {
        // Visit each rule that infers us and add its (new) antecedents to the queue.
        for (Rule<RuleIdT, PropositionT> abducted : abducteds) {
          if (fired.add(abducted) == false) {
            // This is odd... We should not have fired this rule before. Because (a) we only visit
            // each proposition once; (b) each rule has only one consequent; and (c) we are
            // searching by consequent, this should never happen.
            throw new AssertionError("Rule fired twice: " + abducted);
          }

          for (PropositionT antecedent : abducted.getAntecedents()) {
            if (queued.add(antecedent) == true) {
              queue.offer(antecedent);
            }
          }
        }

        // Since we were inferred by some rule, we must be a lemma.
        lemmas.add(next);
      }
    } while (!queue.isEmpty());

    for (PropositionT hypothesis : hypotheses) {
      if (!lemmas.contains(hypothesis)) {
        postulates.add(hypothesis);
      }
    }

    return new AbductiveClosure<>(hypotheses, rules, fired, postulates, lemmas);
  }
}
