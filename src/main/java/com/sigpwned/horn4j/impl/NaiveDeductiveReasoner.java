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

import java.util.HashSet;
import java.util.Set;
import com.sigpwned.horn4j.DeductiveClosure;
import com.sigpwned.horn4j.DeductiveReasoner;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;

public class NaiveDeductiveReasoner<RuleIdT, PropositionT>
    implements DeductiveReasoner<RuleIdT, PropositionT> {

  @Override
  public DeductiveClosure<RuleIdT, PropositionT> deduct(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> rules) {
    Set<PropositionT> satisfied = new HashSet<>(assumptions);
    Set<PropositionT> conclusions = new HashSet<>();
    Set<Rule<RuleIdT, PropositionT>> fired = new HashSet<>();

    boolean changed;
    do {
      changed = false;

      Set<Rule<RuleIdT, PropositionT>> deduceds = rules.findBySatisfiedAntecedents(satisfied);
      for (Rule<RuleIdT, PropositionT> deduced : deduceds) {
        if (fired.add(deduced) == false) {
          continue;
        }

        if (conclusions.add(deduced.getConsequent()) == true) {
          satisfied.add(deduced.getConsequent());
          changed = true;
        }
      }
    } while (changed);

    return new DeductiveClosure<>(assumptions, rules, fired, conclusions);
  }
}
