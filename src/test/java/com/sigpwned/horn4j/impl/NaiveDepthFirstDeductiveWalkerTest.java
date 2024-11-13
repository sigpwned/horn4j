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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.sigpwned.horn4j.DeductiveWalk;
import com.sigpwned.horn4j.DeductiveWalker;
import com.sigpwned.horn4j.DeductiveWalker.Instruction;
import com.sigpwned.horn4j.Rule;
import com.sigpwned.horn4j.util.Lists;
import com.sigpwned.horn4j.util.Sets;

public class NaiveDepthFirstDeductiveWalkerTest {
  @Test
  public void testWalkSimpleRules() {
    // The graph looks like this:
    // @formatter:off
    // A
    // ├── B
    // │   └── D
    // └── C
    //     └── E
    // @formatter:on

    final Set<String> assumptions = new HashSet<>();
    assumptions.add("A");

    // Add some simple rules to the ruleset
    final Set<Rule<String, String>> rules = new HashSet<>();
    rules.add(new Rule<>("B", Sets.of("A"), "B"));
    rules.add(new Rule<>("C", Sets.of("A"), "C"));
    rules.add(new Rule<>("D", Sets.of("B"), "D"));
    rules.add(new Rule<>("E", Sets.of("C"), "E"));

    final Set<String> seen = new LinkedHashSet<>();
    new NaiveDepthFirstDeductiveWalker<String, String>().walk(assumptions,
        new DefaultRuleSet<>(rules), new DeductiveWalker.Visitor<String, String>() {
          @Override
          public Instruction step(DeductiveWalk<String, String> walk) {
            seen.addAll(walk.getConclusions());
            return Instruction.CONTINUE;
          }
        });

    final List<String> actualOrder = new ArrayList<>(seen);

    final int bi = actualOrder.indexOf("B");
    final int ci = actualOrder.indexOf("C");

    List<String> expectedOrder;
    if (bi < ci) {
      // We are depth-first, and we started with B
      expectedOrder = Lists.of("B", "D", "C", "E");
    } else {
      // We are depth-first, and we started with C
      expectedOrder = Lists.of("C", "E", "B", "D");
    }

    assertThat(actualOrder, equalTo(expectedOrder));
  }
}
