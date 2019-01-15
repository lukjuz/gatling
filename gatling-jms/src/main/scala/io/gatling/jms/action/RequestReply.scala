/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
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

package io.gatling.jms.action

import io.gatling.commons.util.Clock

import javax.jms.Message
import io.gatling.commons.validation.Validation
import io.gatling.core.action._
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import io.gatling.jms.client.JmsConnectionPool
import io.gatling.jms.protocol.JmsProtocol
import io.gatling.jms.request._

/**
 * Core JMS Action to handle Request-Reply semantics
 *
 * This handles the core "send"ing of messages. Gatling calls the execute method to trigger a send.
 * This implementation then forwards it on to a tracking actor.
 */
class RequestReply(
    attributes:         JmsAttributes,
    replyDestination:   JmsDestination,
    setJmsReplyTo:      Boolean,
    trackerDestination: Option[JmsDestination],
    protocol:           JmsProtocol,
    jmsConnectionPool:  JmsConnectionPool,
    val statsEngine:    StatsEngine,
    val clock:          Clock,
    val next:           Action,
    throttler:          Throttler,
    throttled:          Boolean
)
  extends JmsAction(attributes, protocol, jmsConnectionPool, throttler, throttled) {

  override val name: String = genName("jmsRequestReply")

  private val jmsReplyDestination = jmsConnection.destination(replyDestination)
  private val messageMatcher = protocol.messageMatcher
  private val replyTimeout = protocol.replyTimeout.getOrElse(0L)
  private val jmsTrackerDestination = trackerDestination.map(dest => jmsConnection.destination(dest)).getOrElse(jmsReplyDestination)

  override protected def beforeSend(requestName: String, session: Session): Validation[Message => Unit] =
    for {
      resolvedReplyDestination <- jmsReplyDestination(session)
      resolvedTrackerDestination <- jmsTrackerDestination(session)
    } yield (message: Message) => {
      if (setJmsReplyTo) {
        message.setJMSReplyTo(resolvedReplyDestination)
      }
      protocol.messageMatcher.prepareRequest(message)

      // notify the tracker that a message was sent
      val matchId = messageMatcher.requestMatchId(message)

      if (logger.underlying.isDebugEnabled) {
        logMessage(s"Message sent JMSMessageID=${message.getJMSMessageID} matchId=$matchId", message)
      }
      // [fl]
      //
      // [/fl]
      val tracker = jmsConnection.tracker(resolvedTrackerDestination, attributes.selector, protocol.listenerThreadCount, messageMatcher)
      tracker.track(matchId, clock.nowMillis, replyTimeout, attributes.checks, session, next, requestName)
    }
}
