/*-
 * =================================LICENSE_START==================================
 * horn4j
 * ====================================SECTION=====================================
 * Copyright (C) 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.horn4j.impl;

import static java.util.stream.Collectors.toSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.horn4j.AbductiveWalk;
import com.sigpwned.horn4j.AbductiveWalker;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.RuleSet;
import com.sigpwned.horn4j.util.Sets;

/**
 * Walks the given rules abductively in <em>breadth-first</em> order.
 * 
 * @param <RuleIdT> the type of the rule identifiers
 * @param <PropositionT> the type of the propositions
 */
public class NaiveAbductiveWalker<RuleIdT, PropositionT>
    implements AbductiveWalker<RuleIdT, PropositionT> {
  private static class WalkState<RuleIdT, PropositionT> {
    public static <RuleIdT, PropositionT> WalkState<RuleIdT, PropositionT> start(
        Set<PropositionT> hypotheses) {
      return new WalkState<>(Sets.copyOf(hypotheses), Sets.of(), Sets.of(), Sets.of());
    }

    private final Set<PropositionT> queue;
    private final Set<PropositionT> lemmas;
    private final Set<PropositionT> postulates;
    private final Set<Rule<RuleIdT, PropositionT>> fired;

    public WalkState(Set<PropositionT> queue, Set<PropositionT> lemmas,
        Set<PropositionT> postulates, Set<Rule<RuleIdT, PropositionT>> fired) {
      this.queue = queue;
      this.lemmas = lemmas;
      this.postulates = postulates;
      this.fired = fired;
    }

    /**
     * @return the queue
     */
    public Set<PropositionT> getQueue() {
      return queue;
    }

    /**
     * @return the lemmas
     */
    public Set<PropositionT> getLemmas() {
      return lemmas;
    }

    /**
     * @return the postulates
     */
    public Set<PropositionT> getPostulates() {
      return postulates;
    }

    /**
     * @return the rules fired during the walk, in order
     */
    public Set<Rule<RuleIdT, PropositionT>> getFired() {
      return fired;
    }

    @Override
    public int hashCode() {
      return Objects.hash(fired, lemmas, postulates, queue);
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
      WalkState other = (WalkState) obj;
      return Objects.equals(fired, other.fired) && Objects.equals(lemmas, other.lemmas)
          && Objects.equals(postulates, other.postulates) && Objects.equals(queue, other.queue);
    }

    @Override
    public String toString() {
      return "WalkState [queue=" + queue + ", lemmas=" + lemmas + ", postulates=" + postulates
          + ", fired=" + fired + "]";
    }
  }

  @Override
  public void walk(Set<PropositionT> hypotheses, RuleSet<RuleIdT, PropositionT> ruleset,
      Visitor<RuleIdT, PropositionT> visitor) {
    if (hypotheses == null)
      throw new NullPointerException();
    if (ruleset == null)
      throw new NullPointerException();
    if (visitor == null)
      throw new NullPointerException();

    Set<WalkState<RuleIdT, PropositionT>> additions = new HashSet<>();
    additions.add(WalkState.start(hypotheses));
    do {
      Set<WalkState<RuleIdT, PropositionT>> newAdditions = null;

      for (WalkState<RuleIdT, PropositionT> walk : additions) {
        Instruction instruction =
            visitor.step(new AbductiveWalk<>(hypotheses, new LinkedHashSet<>(walk.getFired()),
                Sets.union(walk.getPostulates(), walk.getLemmas())));
        switch (instruction) {
          case CONTINUE:
            // Carry on with this walk. Do nothing.
            break;
          case PRUNE:
            // Stop this walk. Do not add it to the new additions.
            continue;
          case STOP:
            // Stop all walks.
            return;
        }

        if (walk.getQueue().isEmpty()) {
          continue;
        }

        if (newAdditions == null) {
          newAdditions = new HashSet<>();
        }

        // TODO How can we make this more efficient?
        // TODO Do we need to separate postulates and lemmas?
        for (PropositionT next : walk.getQueue()) {
          if (walk.getPostulates().contains(next) || walk.getLemmas().contains(next)) {
            continue;
          }

          Set<Rule<RuleIdT, PropositionT>> fireableRules =
              ruleset.findByConsequent(next).stream().collect(toSet());
          if (fireableRules.isEmpty()) {
            Set<PropositionT> newQueue = new HashSet<>(walk.getQueue());
            newQueue.remove(next);

            Set<PropositionT> newPostulates = new HashSet<>(walk.getPostulates());
            newPostulates.add(next);

            newAdditions
                .add(new WalkState<>(newQueue, walk.getLemmas(), newPostulates, walk.getFired()));
          } else {
            for (Rule<RuleIdT, PropositionT> rule : fireableRules) {
              Set<PropositionT> newQueue = new HashSet<>(walk.getQueue());
              newQueue.remove(next);
              newQueue.addAll(rule.getAntecedents());

              Set<PropositionT> newLemmas;
              if (!hypotheses.contains(next)) {
                newLemmas = new HashSet<>(walk.getLemmas());
                newLemmas.add(next);
              } else {
                newLemmas = walk.getLemmas();
              }

              Set<Rule<RuleIdT, PropositionT>> newFired = new HashSet<>(walk.getFired());
              newFired.add(rule);

              newAdditions
                  .add(new WalkState<>(newQueue, newLemmas, walk.getPostulates(), newFired));
            }
          }
        }
      }

      additions = newAdditions;
    } while (additions != null);
  }
}
