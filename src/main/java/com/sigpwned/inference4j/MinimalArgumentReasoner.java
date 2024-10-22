package com.sigpwned.inference4j;

import java.util.Set;

public interface MinimalArgumentReasoner<RuleIdT, PropositionT> {
  /**
   * <p>
   * Determine the minimal arguments that support a given hypothesis. Each minimal argument contains
   * (a) the minimal set of necessary and sufficient propositions to conclude the hypothesis using
   * (b) the minimal set of rules.
   * </p>
   * 
   * <p>
   * If the result contains one element, then it is the unique ("canonical") such minimal solution,
   * and the system is deterministic.
   * </p>
   * 
   * <p>
   * The result will never be empty, since there is always the trivial solution of an argument with
   * the given hypothesis as an assumption and no rules.
   * </p>
   * 
   * @param rules
   * @param hypothesis
   * @return
   */
  public Set<Argument<RuleIdT, PropositionT, Rule<RuleIdT, PropositionT>>> minimalArguments(
      RuleSet<RuleIdT, PropositionT> rules, PropositionT hypothesis);

  /**
   * <p>
   * Determine the minimal arguments that support a given set of hypotheses. Each minimal argument
   * contains (a) the minimal set of necessary and sufficient propositions to conclude the
   * hypotheses using (b) the minimal set of rules.
   * </p>
   * 
   * <p>
   * If the result contains one element, then it is the unique ("canonical") such minimal solution,
   * and the system is deterministic.
   * </p>
   * 
   * <p>
   * The result will never be empty, since there is always the trivial solution of an argument with
   * the given hypotheses as assumptions and no rules.
   * </p>
   * 
   * @param rules
   * @param hypotheses
   * @return
   */
  public Set<Argument<RuleIdT, PropositionT, Rule<RuleIdT, PropositionT>>> minimalArguments(
      RuleSet<RuleIdT, PropositionT> rules, Set<PropositionT> hypotheses);
}
