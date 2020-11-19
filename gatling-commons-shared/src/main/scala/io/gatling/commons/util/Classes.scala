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

package io.gatling.commons.util

import java.{ lang => jl }

import io.gatling.netty.util.StringBuilderPool

object Classes {

  private[util] def appendClassShortName(className: String, sb: jl.StringBuilder): Unit = {
    var bufferedChar: Char = className.charAt(0)
    var offset = 0
    var next = 0
    while ({
      next = className.indexOf('.', offset)
      next != -1
    }) {
      sb.append(bufferedChar).append('.')
      bufferedChar = className.charAt(next + 1)
      offset = next + 2
    }

    if (offset > 0) {
      sb.append(bufferedChar)
    }

    val endOffset = if (className.charAt(className.length - 1) == '$') 1 else 0

    sb.append(className, offset, className.length - endOffset)
  }

  def toClassShortName(className: String): String = {
    val sb = StringBuilderPool.DEFAULT.get()
    appendClassShortName(className, sb)
    sb.toString
  }

  implicit class PimpedClass(val clazz: Class[_]) extends AnyVal {
    def getShortName: String = toClassShortName(clazz.getName)

    def nonAnonSuperclass: Class[_] = {
      var res: Class[_] = clazz
      while (res.isAnonymousClass || res.getName.contains("$anon$")) {
        res = clazz.getSuperclass
      }
      res
    }
  }
}
