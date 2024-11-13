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
import java.util.LinkedHashSet;
import java.util.Set;
import com.sigpwned.horn4j.AbductiveClosureSolver;
import com.sigpwned.horn4j.AbductiveWalk;
import com.sigpwned.horn4j.AbductiveWalker;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;

public class NaiveAbductiveClosureSolver<RuleIdT, PropositionT>
    implements AbductiveClosureSolver<RuleIdT, PropositionT> {

  @Override
  public AbductiveWalk<RuleIdT, PropositionT> abduct(Set<PropositionT> hypotheses,
      RuleSet<RuleIdT, PropositionT> rules) {
    if (hypotheses == null)
      throw new NullPointerException();
    if (rules == null)
      throw new NullPointerException();

    final Set<PropositionT> evidence = new HashSet<>();
    final LinkedHashSet<Rule<RuleIdT, PropositionT>> fired = new LinkedHashSet<>();
    new NaiveAbductiveWalker<RuleIdT, PropositionT>().walk(hypotheses, rules, (walk) -> {
      evidence.addAll(walk.getEvidence());
      fired.addAll(walk.getFired());
      return AbductiveWalker.Instruction.CONTINUE;
    });

    return new AbductiveWalk<RuleIdT, PropositionT>(hypotheses, fired, evidence);
  }
}
