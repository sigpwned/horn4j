package com.sigpwned.inference4j.impl;

import static java.util.stream.Collectors.toSet;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.inference4j.Production;
import com.sigpwned.inference4j.ProductionSet;
import com.sigpwned.inference4j.util.Chain;

public class ProductionSetChain<IdT, PropositionT> extends Chain<ProductionSet<IdT, PropositionT>>
    implements ProductionSet<IdT, PropositionT> {

  @Override
  public Optional<Production<IdT, PropositionT>> deduct(Set<PropositionT> satisfied) {
    return stream().flatMap(ps -> ps.deduct(satisfied).stream()).findAny();
  }

  @Override
  public Set<Production<IdT, PropositionT>> abduct(PropositionT necessary) {
    return stream().flatMap(ps -> ps.abduct(necessary).stream()).collect(toSet());
  }
}


