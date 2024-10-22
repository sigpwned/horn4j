package com.sigpwned.inference4j.impl;

import java.util.HashSet;
import java.util.Set;
import com.sigpwned.inference4j.DeductiveClosure;
import com.sigpwned.inference4j.DeductiveReasoner;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;

public class NaiveDeductiveReasoner<RuleIdT, PropositionT>
    implements DeductiveReasoner<RuleIdT, PropositionT> {

  @Override
  public DeductiveClosure<RuleIdT, PropositionT> deduct(Set<PropositionT> assumptions,
      RuleSet<RuleIdT, PropositionT> rules) {
    Set<PropositionT> satisfied = new HashSet<>(assumptions);
    Set<PropositionT> conclusions = new HashSet<>();
    Set<Rule<RuleIdT, PropositionT>> fired = new HashSet<>();

    boolean changed;
    do {
      changed = false;

      Set<Rule<RuleIdT, PropositionT>> deduceds = rules.deduct(satisfied);
      for (Rule<RuleIdT, PropositionT> deduced : deduceds) {
        if (fired.add(deduced) == false) {
          continue;
        }

        for (PropositionT antecedent : deduced.getAntecedents()) {
          if (conclusions.add(antecedent) == true) {
            changed = true;
          }
        }
      }
    } while (changed);

    return new DeductiveClosure<>(assumptions, rules, fired, conclusions);
  }
}
