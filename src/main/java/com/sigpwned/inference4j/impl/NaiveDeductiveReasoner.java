package com.sigpwned.inference4j.impl;

import java.util.HashSet;
import java.util.Set;
import com.sigpwned.inference4j.DeductiveReasoner;
import com.sigpwned.inference4j.DeductiveClosure;
import com.sigpwned.inference4j.Production;
import com.sigpwned.inference4j.ProductionSet;

public class NaiveDeductiveReasoner<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>>
    implements DeductiveReasoner<RuleIdT, PropositionT, RuleT> {

  @Override
  public DeductiveClosure<RuleIdT, PropositionT, RuleT> deduct(Set<PropositionT> assumptions,
      ProductionSet<RuleIdT, PropositionT, RuleT> rules) {
    Set<PropositionT> satisfied = new HashSet<>(assumptions);
    Set<PropositionT> conclusions = new HashSet<>();
    Set<RuleT> fired = new HashSet<>();

    boolean changed;
    do {
      changed = false;

      Set<RuleT> deduceds = rules.deduct(satisfied);
      for (RuleT deduced : deduceds) {
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
