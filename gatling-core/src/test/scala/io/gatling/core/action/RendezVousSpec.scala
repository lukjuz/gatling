/*
 * Copyright 2011-2020 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.core.action

import io.gatling.AkkaSpec
import io.gatling.commons.util.DefaultClock
import io.gatling.core.session.SessionSpec.EmptySession
import io.gatling.core.stats.StatsEngine

class RendezVousSpec extends AkkaSpec {

  private val clock = new DefaultClock

  "RendezVous" should "block the specified number of sessions until they have all reached it" in {
    val rendezVous = RendezVous(3, system, mock[StatsEngine], clock, new ActorDelegatingAction("next", self))

    rendezVous ! EmptySession
    expectNoMessage(remainingOrDefault)

    rendezVous ! EmptySession
    expectNoMessage(remainingOrDefault)

    rendezVous ! EmptySession
    expectMsgAllOf(EmptySession, EmptySession, EmptySession)

    rendezVous ! EmptySession
    expectMsg(EmptySession)
  }
}
