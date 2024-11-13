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
package com.sigpwned.horn4j;

import java.util.Set;

public interface AbductiveWalker<RuleIdT, PropositionT> {
  public static enum Instruction {
    /**
     * Continue walking with no change.
     */
    CONTINUE,

    /**
     * Stop walking this walk only. Continue with other walks.
     */
    PRUNE,

    /**
     * Stop walking all walks. The walk is considered complete, and no more walks will be started.
     */
    STOP;
  }

  @FunctionalInterface
  public static interface Visitor<RuleIdT, PropositionT> {
    /**
     * Called for each step of each walk. The walk can be stopped by returning {@code false}.
     * 
     * The given walk is guaranteed to have a unique order of rules walked. As a result, the
     * consequences are ordered too. However, that does not mean that every walk is unique according
     * to the {@DeductiveWalk#equals(Object)} method.
     * 
     * @param walk The current walk
     * @return {@code true} if the walk should continue, {@code false} otherwise
     */
    public Instruction step(AbductiveWalk<RuleIdT, PropositionT> walk);
  }

  public void walk(Set<PropositionT> hypotheses, RuleSet<RuleIdT, PropositionT> rules,
      Visitor<RuleIdT, PropositionT> visitor);
}
