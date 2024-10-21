package com.sigpwned.inference4j;

import java.util.Set;

public interface AbductiveReasoner<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>> {
  public AbductiveClosure<RuleIdT, PropositionT, RuleT> abduct(Set<PropositionT> assumptions,
      ProductionSet<RuleIdT, PropositionT, RuleT> rules);
}
