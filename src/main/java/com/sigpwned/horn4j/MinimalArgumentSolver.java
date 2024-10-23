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
package com.sigpwned.horn4j;

import java.util.Set;

public interface MinimalArgumentSolver<RuleIdT, PropositionT> {
  /**
   * <p>
   * Determine the minimal arguments that support a given hypothesis. Each minimal argument contains
   * (a) the minimal set of necessary and sufficient propositions to conclude the hypothesis using
   * (b) the minimal set of rules.
   * </p>
   * 
   * <p>
   * If the result contains one element, then it is the unique ("canonical") such minimal solution,
   * and the system is deterministic.
   * </p>
   * 
   * <p>
   * The result will never be empty, since there is always the trivial solution of an argument with
   * the given hypothesis as an assumption and no rules.
   * </p>
   * 
   * @param rules
   * @param hypothesis
   * @return
   */
  public Set<Argument<RuleIdT, PropositionT>> minimalArguments(RuleSet<RuleIdT, PropositionT> rules,
      PropositionT hypothesis);

  /**
   * <p>
   * Determine the minimal arguments that support a given set of hypotheses. Each minimal argument
   * contains (a) the minimal set of necessary and sufficient propositions to conclude the
   * hypotheses using (b) the minimal set of rules.
   * </p>
   * 
   * <p>
   * If the result contains one element, then it is the unique ("canonical") such minimal solution,
   * and the system is deterministic.
   * </p>
   * 
   * <p>
   * The result will never be empty, since there is always the trivial solution of an argument with
   * the given hypotheses as assumptions and no rules.
   * </p>
   * 
   * @param rules
   * @param hypotheses
   * @return
   */
  public Set<Argument<RuleIdT, PropositionT>> minimalArguments(RuleSet<RuleIdT, PropositionT> rules,
      Set<PropositionT> hypotheses);
}
