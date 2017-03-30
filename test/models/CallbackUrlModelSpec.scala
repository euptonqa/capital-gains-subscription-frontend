/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import uk.gov.hmrc.play.test.UnitSpec

class CallbackUrlModelSpec extends UnitSpec {

  "Creating a CallbackUrlModel" when {

    "passed a valid context url" should {
      val url = "/context/test"

      "return a valid model" in {
        val model = CallbackUrlModel(url)

        model.url shouldBe "/context/test"
      }
    }

    "passed an invalid non-context url" should {
      val url = "http://www.google.com"

      "return an exception" in {
        val exception = intercept[Exception] {CallbackUrlModel(url)}

        exception.getMessage shouldBe "requirement failed: Failed to bind as a URI"
      }
    }

    "passed a valid non-context url" should {
      val url = "http://localhost:9000/test/route"

      "return a valid model" in {
        val model = CallbackUrlModel(url)

        model.url shouldBe "http://localhost:9000/test/route"
      }
    }
  }
}
