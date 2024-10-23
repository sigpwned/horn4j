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
package com.sigpwned.inference4j.impl;

import com.sigpwned.inference4j.MinimalArgumentReasoner;
import com.sigpwned.inference4j.MinimalArgumentReasonerTestBase;

public class NaiveMinimalArgumentReasonerTest extends MinimalArgumentReasonerTestBase {
  @Override
  public MinimalArgumentReasoner<String, String> newMinimalArgumentReasoner() {
    return new NaiveMinimalArgumentReasoner<>();
  }
}