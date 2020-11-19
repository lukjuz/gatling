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

package io.gatling.core.check.css

import com.typesafe.scalalogging.StrictLogging
import jodd.lagarto.{ LagartoParser, LagartoParserConfig }
import jodd.lagarto.dom.{ Document, LagartoDOMBuilder, LagartoDOMBuilderTagVisitor, LagartoDomBuilderConfig }

class InfoLogLagartoDOMBuilderTagVisitor(domBuilder: LagartoDOMBuilder) extends LagartoDOMBuilderTagVisitor(domBuilder) with StrictLogging {

  override def errorEnabled(): Boolean =
    domBuilder.getConfig.isCollectErrors || logger.underlying.isInfoEnabled

  override def error(message: String): Unit = {
    rootNode.addError(message)
    logger.info(message)
  }
}

class InfoLogLagartoDOMBuilder(config: LagartoDomBuilderConfig) extends LagartoDOMBuilder(config) {

  override def parseWithLagarto(lagartoParser: LagartoParser): Document = {
    val domBuilderTagVisitor = new InfoLogLagartoDOMBuilderTagVisitor(this)
    lagartoParser.parse(domBuilderTagVisitor)
    domBuilderTagVisitor.getDocument
  }
}

object Jodd {

  private val ParserConfig =
    new LagartoParserConfig()
      .setEnableConditionalComments(false)
      .setEnableRawTextModes(false)

  private val DomBuilderConfig = {
    val config = new LagartoDomBuilderConfig()
    config.setParserConfig(ParserConfig)
    config
  }

  def newLagartoDomBuilder: LagartoDOMBuilder =
    new InfoLogLagartoDOMBuilder(DomBuilderConfig)

  def newLagartoParser(chars: Array[Char]): LagartoParser =
    new LagartoParser(ParserConfig, chars)
}
