package com.sigpwned.inference4j.impl;

import static java.util.stream.Collectors.toSet;
import java.util.Set;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;
import com.sigpwned.inference4j.util.Chain;

public class ProductionSetChain<IdT, PropositionT> extends Chain<RuleSet<IdT, PropositionT>>
    implements RuleSet<IdT, PropositionT> {

  @Override
  public Set<Rule<IdT, PropositionT>> deduct(Set<PropositionT> satisfied) {
    return stream().flatMap(ps -> ps.deduct(satisfied).stream()).collect(toSet());
  }

  @Override
  public Set<Rule<IdT, PropositionT>> abduct(PropositionT necessary) {
    return stream().flatMap(ps -> ps.abduct(necessary).stream()).collect(toSet());
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findByAntecedents(Set<PropositionT> antecedents) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findByConsequent(PropositionT consequent) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<Rule<IdT, PropositionT>> findBySignature(Set<PropositionT> antecedents,
      PropositionT consequent) {
    // TODO Auto-generated method stub
    return null;
  }
}


