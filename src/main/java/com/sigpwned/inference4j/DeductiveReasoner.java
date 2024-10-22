package com.sigpwned.inference4j;

import java.util.Set;

public interface DeductiveReasoner<RuleIdT, PropositionT> {
  public DeductiveClosure<RuleIdT, PropositionT> deduct(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> rules);
}
