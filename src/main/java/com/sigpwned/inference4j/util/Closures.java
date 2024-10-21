package com.sigpwned.inference4j.util;

import static java.util.stream.Collectors.toSet;
import java.util.ArrayList;
import java.util.Set;
import com.sigpwned.inference4j.Closure;
import com.sigpwned.inference4j.InferenceEngine;
import com.sigpwned.inference4j.Production;
import com.sigpwned.inference4j.ProductionSet;
import com.sigpwned.inference4j.impl.DefaultProductionSet;

public final class Closures {
  private Closures() {}

  public static <IdT, PropositionT> Closure<IdT, PropositionT> prune(
      Closure<IdT, PropositionT> closure, Set<PropositionT> anchors) {
    if (!closure.getConclusions().containsAll(anchors)) {
      throw new IllegalArgumentException("anchors must be a subset of conclusions");
    }

    final ProductionSet<IdT, PropositionT> productionSet =
        new DefaultProductionSet<>(new ArrayList<>(closure.getRules()));

    final InferenceEngine<IdT, PropositionT> engine = new InferenceEngine<>(productionSet);

    final Set<PropositionT> abductiveClosure = engine.abductiveClosure(anchors);

    Set<PropositionT> prunedAssumptions =
        closure.getAssumptions().stream().filter(abductiveClosure::contains).collect(toSet());
    Set<Production<IdT, PropositionT>> prunedProductions =
        closure.getRules().stream().filter(p -> abductiveClosure.contains(p.getConsequent())
            && abductiveClosure.containsAll(p.getAntecedents())).collect(toSet());
    Set<PropositionT> prunedConclusions =
        closure.getConclusions().stream().filter(abductiveClosure::contains).collect(toSet());

    return new Closure<>(prunedAssumptions, prunedProductions, prunedConclusions);
  }
}
