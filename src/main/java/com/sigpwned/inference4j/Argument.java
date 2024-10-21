package com.sigpwned.inference4j;

import static java.util.Collections.unmodifiableSet;
import java.util.Set;

public class Argument<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>> {
  /**
   * The propositions assumed to be true as the basis for this argument.
   */
  private final Set<PropositionT> assumptions;

  /**
   * The rules that are used to derive new propositions in this argument.
   */
  private final Set<RuleT> rules;

  public Argument(Set<PropositionT> assumptions, Set<RuleT> rules) {
    this.assumptions = unmodifiableSet(assumptions);
    this.rules = unmodifiableSet(rules);

    // The assumptions should not appear as a consequent in the rules
    assert assumptions.stream()
        .noneMatch(a -> rules.stream().anyMatch(r -> r.getConsequent().equals(a)));

    // Additionally:
    // - We don't check, but the antecedents of each rule should be either (a) an assumption, or (b)
    // the consequent of another rule "earlier" in the argument.
  }

  /**
   * @return the assumptions
   */
  public Set<PropositionT> getAssumptions() {
    return assumptions;
  }

  /**
   * @return the rules
   */
  public Set<RuleT> getRules() {
    return rules;
  }
}
