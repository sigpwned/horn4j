package com.sigpwned.inference4j;

import java.util.Set;

public interface AbductiveReasoner<RuleIdT, PropositionT> {
  public AbductiveClosure<RuleIdT, PropositionT> abduct(Set<PropositionT> hypotheses,
      RuleSet<RuleIdT, PropositionT> rules);
}
