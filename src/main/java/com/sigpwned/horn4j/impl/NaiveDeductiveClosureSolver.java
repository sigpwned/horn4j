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

import static java.util.Objects.requireNonNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import com.sigpwned.horn4j.DeductiveClosureSolver;
import com.sigpwned.horn4j.DeductiveWalk;
import com.sigpwned.horn4j.DeductiveWalker;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;

public class NaiveDeductiveClosureSolver<RuleIdT, PropositionT>
    implements DeductiveClosureSolver<RuleIdT, PropositionT> {
  private final Supplier<DeductiveWalker<RuleIdT, PropositionT>> deductiveWalkerFactory;

  public NaiveDeductiveClosureSolver() {
    this(NaiveDepthFirstDeductiveWalker::new);
  }

  public NaiveDeductiveClosureSolver(
      Supplier<DeductiveWalker<RuleIdT, PropositionT>> deductiveWalkerFactory) {
    this.deductiveWalkerFactory = requireNonNull(deductiveWalkerFactory);
  }

  @Override
  public DeductiveWalk<RuleIdT, PropositionT> deduct(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> rules) {
    if (assumptions == null)
      throw new NullPointerException();
    if (rules == null)
      throw new NullPointerException();

    final Set<PropositionT> conclusions = new HashSet<>();
    final LinkedHashSet<Rule<RuleIdT, PropositionT>> fired = new LinkedHashSet<>();
    getDeductiveWalkerFactory().get().walk(assumptions, rules, (walk) -> {
      conclusions.addAll(walk.getConclusions());
      fired.addAll(walk.getFired());
      return DeductiveWalker.Instruction.CONTINUE;
    });

    return new DeductiveWalk<>(assumptions, fired, conclusions);
  }

  protected Supplier<DeductiveWalker<RuleIdT, PropositionT>> getDeductiveWalkerFactory() {
    return deductiveWalkerFactory;
  }
}
