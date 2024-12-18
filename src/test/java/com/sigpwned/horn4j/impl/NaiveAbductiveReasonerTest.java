/*-
 * =================================LICENSE_START==================================
 * inference4j
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

import com.sigpwned.horn4j.AbductiveClosureSolver;
import com.sigpwned.horn4j.AbductiveReasonerTestBase;

public class NaiveAbductiveReasonerTest extends AbductiveReasonerTestBase {
  @Override
  public AbductiveClosureSolver<String, String> newAbductiveReasoner() {
    return new NaiveAbductiveClosureSolver<String, String>();
  }
}
