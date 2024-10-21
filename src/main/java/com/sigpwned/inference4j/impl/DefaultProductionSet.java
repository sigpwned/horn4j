package com.sigpwned.inference4j.impl;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.inference4j.Production;
import com.sigpwned.inference4j.ProductionSet;

public class DefaultProductionSet<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>>
    implements ProductionSet<RuleIdT, PropositionT, RuleT> {
  private final List<RuleT> productions;

  public DefaultProductionSet(List<RuleT> productions) {
    this.productions = unmodifiableList(productions);
  }

  @Override
  public Optional<RuleT> deduct(Set<PropositionT> satisfied) {
    return getProductions().stream().filter(p -> satisfied.containsAll(p.getAntecedents()))
        .filter(p -> !satisfied.contains(p.getConsequent())).findAny();
  }

  @Override
  public Set<RuleT> abduct(PropositionT necessary) {
    return getProductions().stream().filter(p -> p.getConsequent().equals(necessary))
        .collect(toSet());
  }

  @Override
  public Set<RuleT> findByAntecedents(Set<PropositionT> antecedents) {
    return getProductions().stream().filter(p -> p.getAntecedents().equals(antecedents))
        .collect(toSet());
  }

  @Override
  public Set<RuleT> findByConsequent(PropositionT consequent) {
    return getProductions().stream().filter(p -> p.getConsequent().equals(consequent))
        .collect(toSet());
  }

  @Override
  public Set<RuleT> findBySignature(Set<PropositionT> antecedents, PropositionT consequent) {
    return getProductions().stream().filter(p -> p.getAntecedents().equals(antecedents))
        .filter(p -> p.getConsequent().equals(consequent)).collect(toSet());
  }

  private List<RuleT> getProductions() {
    return productions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(productions);
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
    DefaultProductionSet other = (DefaultProductionSet) obj;
    return Objects.equals(productions, other.productions);
  }

  @Override
  public String toString() {
    return "DefaultProductionSet [productions=" + productions + "]";
  }
}
