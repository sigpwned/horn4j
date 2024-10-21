package com.sigpwned.inference4j;

import java.util.Set;

public interface ProductionSet<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>> {
  /**
   * <p>
   * Perform one step of deductive reasoning. Given the set of propositions that are satisfied,
   * choose one production from this set such that: (a) the antecedents of the production are
   * satisfied, and (b) the consequent of the production is not satisfied.
   * </p>
   * 
   * <p>
   * Colloquially, this is "forward inference."
   * </p>
   */
  public Set<RuleT> deduct(Set<PropositionT> satisfied);

  /**
   * <p>
   * Perform one step of abductive reasoning. Given a proposition that is necessary, return all of
   * the productions that have that proposition as a consequent.
   * </p>
   * 
   * <p>
   * Colloquially, this is "backward inference."
   * </p>
   */
  public Set<RuleT> abduct(PropositionT necessary);

  public Set<RuleT> findByAntecedents(Set<PropositionT> antecedents);

  public Set<RuleT> findByConsequent(PropositionT consequent);

  public Set<RuleT> findBySignature(Set<PropositionT> antecedents, PropositionT consequent);
}
