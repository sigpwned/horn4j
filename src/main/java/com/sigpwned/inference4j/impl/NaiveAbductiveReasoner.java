package com.sigpwned.inference4j.impl;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import com.sigpwned.inference4j.AbductiveClosure;
import com.sigpwned.inference4j.AbductiveReasoner;
import com.sigpwned.inference4j.Rule;
import com.sigpwned.inference4j.RuleSet;

public class NaiveAbductiveReasoner<RuleIdT, PropositionT>
    implements AbductiveReasoner<RuleIdT, PropositionT> {

  @Override
  public AbductiveClosure<RuleIdT, PropositionT> abduct(Set<PropositionT> hypotheses,
      RuleSet<RuleIdT, PropositionT> rules) {
    Set<PropositionT> postulates = new HashSet<>();
    Set<PropositionT> lemmas = new HashSet<>();
    Set<Rule<RuleIdT, PropositionT>> fired = new HashSet<>();

    Queue<PropositionT> queue = new ArrayDeque<>(hypotheses);
    Set<PropositionT> queued = new HashSet<>(hypotheses);
    do {
      PropositionT next = queue.poll();

      // TODO Should we do some kind of session to make calculation in RuleSet more efficient?
      Set<Rule<RuleIdT, PropositionT>> abducteds = rules.abduct(next);
      if (abducteds.isEmpty()) {
        // Since no rules infer us, we must be a postulate.
        postulates.add(next);
      } else {
        // Visit each rule that infers us and add its (new) antecedents to the queue.
        for (Rule<RuleIdT, PropositionT> abducted : abducteds) {
          if (fired.add(abducted) == false) {
            // This is odd... We should not have fired this rule before. Because (a) we only visit
            // each proposition once; (b) each rule has only one consequent; and (c) we are
            // searching by consequent, this should never happen.
            throw new AssertionError("Rule fired twice: " + abducted);
          }

          for (PropositionT antecedent : abducted.getAntecedents()) {
            if (queued.add(antecedent) == true) {
              queue.offer(antecedent);
            }
          }
        }

        // Since we were inferred by some rule, we must be a lemma.
        lemmas.add(next);
      }
    } while (!queue.isEmpty());

    return new AbductiveClosure<>(hypotheses, rules, fired, postulates, lemmas);
  }
}
