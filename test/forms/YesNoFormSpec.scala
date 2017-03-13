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

package forms

import data.MessageLookup
import models.YesNoModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class YesNoFormSpec extends UnitSpec with OneAppPerSuite {

  lazy val form = app.injector.instanceOf[YesNoForm].yesNoForm

  "Creating a form from a model" should {

    "return a valid form when the model contains a true" in {
      val model = YesNoModel(true)
      lazy val result = form.fill(model)

      result.data shouldBe Map("response" -> "Yes")
    }

    "return a valid form when the model contains a false" in {
      val model = YesNoModel(false)

      lazy val result = form.fill(model)

      result.data shouldBe Map("response" -> "No")
    }
  }

  "Creating a form from a map" when {

    "supplied with no response" should {
      val map = Map("response" -> "")
      lazy val result = form.bind(map)

      "have no model" in {
        result.value.isDefined shouldBe false
      }

      "have one error" in {
        result.errors.size shouldBe 1
      }

      s"have the error message '${MessageLookup.Errors.errorMandatory}'" in {
        result.error("response").get.message shouldBe MessageLookup.Errors.errorMandatory
      }
    }

    "supplied with a valid response 'Yes'" should {
      val map = Map("response" -> "Yes")
      lazy val result = form.bind(map)

      "have a valid model" in {
        result.value.isDefined shouldBe true
      }

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a value of true" in {
        result.value.get.response shouldBe true
      }
    }

    "supplied with a valid response 'No'" should {
      val map = Map("response" -> "No")
      lazy val result = form.bind(map)

      "have a valid model" in {
        result.value.isDefined shouldBe true
      }

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a value of false" in {
        result.value.get.response shouldBe false
      }
    }
  }
}
