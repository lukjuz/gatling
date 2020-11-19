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

import io.gatling.BaseSpec
import io.gatling.commons.stats.KO
import io.gatling.commons.util.Clock
import io.gatling.core.session.{ GroupBlock, Session }
import io.gatling.core.session.SessionSpec.EmptySession
import io.gatling.core.stats.StatsEngine

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

class ExitHereSpec extends BaseSpec {

  "ExitHereIfFailed" should "send the session to the next action if the session was not failed" in {
    val exit = mock[Action]
    val next = mock[Action]
    val statsEngine = mock[StatsEngine]
    val clock = mock[Clock]
    val exitHereIfFailed = new ExitHere(ExitHere.ExitHereOnFailedCondition, exit, statsEngine, clock, next)

    exitHereIfFailed ! EmptySession
    verify(next) ! EmptySession
    verify(exit, never) ! any[Session]
  }

  it should "trigger with higher precedence than tryMax" in {
    val exit = mock[Action]
    val next = mock[Action]
    val statsEngine = mock[StatsEngine]
    val clock = mock[Clock]
    val exitHereIfFailed = new ExitHere(ExitHere.ExitHereOnFailedCondition, exit, statsEngine, clock, next)

    val sessionWithTryMax = EmptySession.enterTryMax("loop", next).markAsFailed

    exitHereIfFailed ! sessionWithTryMax

    verify(exit) ! sessionWithTryMax
    verify(next, never) ! any[Session]
  }

  it should "also log a group end if the user was inside a group" in {
    val exit = mock[Action]
    val next = mock[Action]
    val statsEngine = mock[StatsEngine]
    val clock = mock[Clock]
    when(clock.nowMillis).thenReturn(1)
    val exitHereIfFailed = new ExitHere(ExitHere.ExitHereOnFailedCondition, exit, statsEngine, clock, next)

    val sessionWithGroup = EmptySession.enterGroup("group", 0).markAsFailed

    exitHereIfFailed ! sessionWithGroup

    verify(exit) ! sessionWithGroup
    verify(next, never) ! any[Session]
    verify(statsEngine).logGroupEnd(sessionWithGroup.scenario, GroupBlock(List("group"), 0, 0, KO), 1)
  }
}
