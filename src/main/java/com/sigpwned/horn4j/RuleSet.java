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

public interface RuleSet<RuleIdT, PropositionT> {
  /**
   * Find all rules such that the rules' antecedents are exactly the given propositions.
   */
  public Set<Rule<RuleIdT, PropositionT>> findByExactAntecedents(Set<PropositionT> propositions);

  /**
   * Find all rules such that the rules' antecedents are a subset of the given propositions.
   */
  public Set<Rule<RuleIdT, PropositionT>> findBySatisfiedAntecedents(
      Set<PropositionT> propositions);

  /**
   * Find all rules such that the rules' consequents are equal to the given proposition.
   */
  public Set<Rule<RuleIdT, PropositionT>> findByConsequent(PropositionT proposition);

  /**
   * Find all rules such that the rules' antecedents are a subset of the given antecedents and the
   * rules' consequents are equal to the given consequent.
   */
  public Set<Rule<RuleIdT, PropositionT>> findBySignature(Set<PropositionT> antecedents,
      PropositionT consequent);
}
