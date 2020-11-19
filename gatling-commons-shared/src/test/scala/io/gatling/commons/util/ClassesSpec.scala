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

import io.gatling.commons.util.Classes._

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class ClassesSpec extends AnyFlatSpecLike with Matchers {

  "getShortName" should "shorten Class name" in {
    classOf[java.util.concurrent.TimeoutException].getShortName shouldBe "j.u.c.TimeoutException"
  }

  "toClassShortName" should "shorten String with package" in {
    toClassShortName("foo.bar.baz.Foo") shouldBe "f.b.b.Foo"
  }

  it should "leave String without package as is" in {
    toClassShortName("Foo") shouldBe "Foo"
  }

  it should "drop object trailing $" in {
    toClassShortName("Foo$") shouldBe "Foo"
  }
}
