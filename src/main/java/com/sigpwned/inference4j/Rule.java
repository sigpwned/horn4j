package com.sigpwned.inference4j;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.Set;

/**
 * An inference rule in the form of {@code (a AND b AND c AND ...)} &rarr; {@code x}, i.e. a
 * <a href="https://en.wikipedia.org/wiki/Horn_clause">horn clause</a>.
 * 
 * @param <IdT> The type of the rule's identifier.
 * @param <PropositionT> The type of the propositions in the rule.
 */
public class Rule<IdT, PropositionT> {
  private final IdT id;
  private final Set<PropositionT> antecedents;
  private final PropositionT consequent;

  public Rule(IdT id, Set<PropositionT> antecedents, PropositionT consequent) {
    this.id = requireNonNull(id);
    this.antecedents = unmodifiableSet(antecedents);
    this.consequent = requireNonNull(consequent);

    // Ensure that the antecedents do not contain the consequent. (a AND X) -> a is a tautology.
    assert !antecedents.contains(consequent);
  }

  /**
   * @return the id
   */
  public IdT getId() {
    return id;
  }

  /**
   * @return the antecedents
   */
  public Set<PropositionT> getAntecedents() {
    return antecedents;
  }

  /**
   * @return the consequent
   */
  public PropositionT getConsequent() {
    return consequent;
  }

  @Override
  public int hashCode() {
    return Objects.hash(antecedents, consequent, id);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Rule other = (Rule) obj;
    return Objects.equals(antecedents, other.antecedents)
        && Objects.equals(consequent, other.consequent) && Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "Rule [id=" + id + ", antecedents=" + antecedents + ", consequent=" + consequent + "]";
  }
}
