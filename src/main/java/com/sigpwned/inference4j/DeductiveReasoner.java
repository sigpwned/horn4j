package com.sigpwned.inference4j;

import java.util.Set;

public interface DeductiveReasoner<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>> {
  public DeductiveClosure<RuleIdT, PropositionT, RuleT> deduct(Set<PropositionT> assumptions,
      ProductionSet<RuleIdT, PropositionT, RuleT> rules);
}
