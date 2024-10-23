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
package com.sigpwned.inference4j.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import java.util.Collections;
import java.util.Set;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;
import com.sigpwned.inference4j.util.Chain;

public class RuleSetChain<IdT, PropositionT> extends Chain<RuleSet<IdT, PropositionT>>
    implements RuleSet<IdT, PropositionT> {

  @Override
  public Set<Rule<IdT, PropositionT>> findByConsequent(PropositionT consequent) {
    return stream().flatMap(rules -> rules.findByConsequent(consequent).stream())
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findBySignature(Set<PropositionT> antecedents,
      PropositionT consequent) {
    return stream().flatMap(rules -> rules.findBySignature(antecedents, consequent).stream())
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findByExactAntecedents(Set<PropositionT> propositions) {
    return stream().flatMap(rules -> rules.findByExactAntecedents(propositions).stream())
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findBySatisfiedAntecedents(Set<PropositionT> propositions) {
    return stream().flatMap(rules -> rules.findBySatisfiedAntecedents(propositions).stream())
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
  }
}


