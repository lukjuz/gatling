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

package io.gatling.http.check

import java.util.{ Map => JMap }

import io.gatling.commons.validation.Validation
import io.gatling.core.check.{ Check, CheckResult }
import io.gatling.core.session.Session
import io.gatling.http.response.HttpFailure

/**
 * This class serves as model for the HTTP-specific checks
 *
 * @param wrapped the underlying check
 * @param scope the part of the response this check targets
 */
final case class ErrorCheck(wrapped: Check[HttpFailure], scope: HttpCheckScope) extends Check[HttpFailure] {
  override def check(error: HttpFailure, session: Session, preparedCache: JMap[Any, Any]): Validation[CheckResult] =
    wrapped.check(error, session, preparedCache)
}