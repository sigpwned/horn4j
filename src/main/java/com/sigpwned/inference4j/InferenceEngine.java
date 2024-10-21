package com.sigpwned.inference4j;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import com.sigpwned.inference4j.util.Lists;
import com.sigpwned.inference4j.util.Sets;

public class InferenceEngine<RuleIdT, PropositionT, RuleT extends Production<RuleIdT, PropositionT>> {
  private final ProductionSet<RuleIdT, PropositionT, RuleT> productionSet;

  public InferenceEngine(ProductionSet<RuleIdT, PropositionT, RuleT> productionSet) {
    this.productionSet = requireNonNull(productionSet);
  }

  public void abduct(Set<PropositionT> axioms,
      AbductionListener<RuleIdT, PropositionT, RuleT> listener) {
    doAbduct(axioms, listener);
  }

  /**
   * Given a set of axioms, perform all possible deductive steps, transitively, and return the set
   * of all satisfied propositions, including the original axioms, i.e., the transitive closure.
   */
  private Set<PropositionT> doAbduct(Set<PropositionT> conclusions,
      AbductionListener<RuleIdT, PropositionT, RuleT> listener) {
    Set<PropositionT> result = new HashSet<>(conclusions);

    Queue<PropositionT> queue = new ArrayDeque<>(conclusions);
    do {
      PropositionT next = queue.poll();

      Set<RuleT> productions = getProductionSet().abduct(next);
      for (RuleT production : productions) {
        listener.abduction(production);
        for (PropositionT antecedent : production.getAntecedents()) {
          if (result.add(antecedent)) {
            queue.offer(antecedent);
          }
        }
      }
    } while (!queue.isEmpty());

    return unmodifiableSet(result);
  }

  /**
   * Given a set of axioms, perform all possible deductive steps, transitively, and return the set
   * of all satisfied propositions, including the original axioms, i.e., the transitive closure.
   */
  public Set<PropositionT> abductiveClosure(Set<PropositionT> axioms) {
    return doAbduct(axioms, new AbductionListener<RuleIdT, PropositionT, RuleT>() {
      @Override
      public void abduction(RuleT production) {}
    });
  }

  /**
   * Given a set of axioms, perform all possible deductive steps, transitively.
   */
  public void deduct(Set<PropositionT> axioms,
      DeductionListener<RuleIdT, PropositionT, RuleT> listener) {
    doDeduct(axioms, listener);
  }

  /**
   * Given a set of axioms, perform all possible deductive steps, transitively, and return the set
   * of all satisfied propositions, including the original axioms, i.e., the transitive closure.
   */
  private Closure<RuleIdT, PropositionT, RuleT> doDeduct(Set<PropositionT> assumptions,
      DeductionListener<RuleIdT, PropositionT, RuleT> listener) {
    final Set<PropositionT> conclusions = new HashSet<>(assumptions);
    final Set<RuleT> rules = new LinkedHashSet<>();

    boolean changed;
    do {
      changed = false;

      // TODO How do we avoid applying the same rule more than once?
      final RuleT next = getProductionSet().deduct(conclusions).orElse(null);

      if (next != null) {
        listener.deduction(next);
        rules.add(next);
        conclusions.add(next.getConsequent());
        changed = true;
      }
    } while (changed);

    return new Closure<RuleIdT, PropositionT, RuleT>(assumptions, rules, conclusions);
  }

  /**
   * Given a set of axioms, perform all possible deductive steps, transitively, and return the set
   * of all satisfied propositions, including the original axioms, i.e., the transitive closure.
   */
  public Closure<RuleIdT, PropositionT, RuleT> deductiveClosure(Set<PropositionT> axioms) {
    return doDeduct(axioms, new DeductionListener<RuleIdT, PropositionT, RuleT>() {
      @Override
      public void deduction(RuleT production) {}
    });
  }

  public Set<Set<PropositionT>> necessary(PropositionT consequent) {
    return doNecessary(consequent, new HashSet<>());
  }

  /**
   * Recursively finds all minimal sets of propositions that can derive the given proposition.
   *
   * @param proposition the proposition to derive
   * @param visited the set of propositions already visited to prevent cycles
   * @return a list of minimal sets of propositions that can derive the given proposition
   */
  private Set<Set<PropositionT>> doNecessary(PropositionT proposition, Set<PropositionT> visited) {
    if (visited.contains(proposition)) {
      // Avoid cycles in the production rules
      return Set.of();
    }
    visited.add(proposition);
    try {
      Set<RuleT> productionsForConsequent = getProductionSet().abduct(proposition);

      if (productionsForConsequent.isEmpty()) {
        // This proposition is axiomatic. It cannot be inferred from any others.
        return Set.of(Set.of(proposition));
      }

      Set<Set<PropositionT>> result = new HashSet<>();

      productions: for (Production<RuleIdT, PropositionT> production : productionsForConsequent) {
        // List of minimal sets for each antecedent
        List<Set<Set<PropositionT>>> antecedentSetsList = new ArrayList<>();

        for (PropositionT antecedent : production.getAntecedents()) {
          Set<Set<PropositionT>> antecedentMinimalSets = doNecessary(antecedent, visited);
          if (antecedentMinimalSets.isEmpty()) {
            // If any antecedent cannot be satisfied, skip this production
            continue productions;
          }
          antecedentSetsList.add(new HashSet<>(antecedentMinimalSets));
        }

        // Use cartesianProduct to combine minimal sets from antecedents
        result.addAll(
            Lists.cartesianProduct(antecedentSetsList).stream().map(Sets::union).collect(toSet()));
      }

      // Remove supersets to keep only minimal sets
      return result.stream()
          .filter(set -> result.stream().noneMatch(
              other -> other != set && other.size() < set.size() && set.containsAll(other)))
          .collect(toSet());
    } finally {
      visited.remove(proposition);
    }
  }

  /**
   * Returns a set containing all the minimal sets of propositions that would cause all the given
   * consequents to be satisfied under this engine's ProductionSet.
   *
   * @param consequents the set of propositions to satisfy
   * @return a set of minimal sets of propositions that lead to all the consequents
   */
  public Set<Set<PropositionT>> necessary(Set<PropositionT> consequents) {
    // List to hold the minimal sets for each consequent
    List<Set<Set<PropositionT>>> antecedentSetsList = new ArrayList<>();

    for (PropositionT consequent : consequents) {
      Set<Set<PropositionT>> minimalSetsForConsequent = necessary(consequent);
      if (minimalSetsForConsequent.isEmpty()) {
        // If any consequent cannot be satisfied, return empty set
        return Set.of();
      }
      antecedentSetsList.add(minimalSetsForConsequent);
    }

    // Compute the Cartesian product of the minimal sets for each consequent
    Set<Set<PropositionT>> result =
        Lists.cartesianProduct(antecedentSetsList).stream().map(Sets::union).collect(toSet());

    // Remove supersets to keep only minimal sets
    return result.stream()
        .filter(set -> result.stream().noneMatch(
            other -> other != set && other.size() < set.size() && set.containsAll(other)))
        .collect(toSet());
  }



  public boolean entails(Set<PropositionT> assumptions, PropositionT consequent) {
    return entails(assumptions, Set.of(consequent));
  }

  public boolean entails(Set<PropositionT> assumptions, Set<PropositionT> consequents) {
    // TODO This is a very naive implementation.
    return deductiveClosure(assumptions).getConclusions().containsAll(consequents);
  }

  private ProductionSet<RuleIdT, PropositionT, RuleT> getProductionSet() {
    return productionSet;
  }
}
