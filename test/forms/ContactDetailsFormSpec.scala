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

import assets.MessageLookup.Errors
import models.ContactDetailsModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class ContactDetailsFormSpec extends UnitSpec with OneAppPerSuite {

  "Creating a form" when {
    val form = app.injector.instanceOf[ContactDetailsForm]

    "provided with a valid map" should {
      val map = Map("contactName" -> "Name", "telephone" -> "11111111111", "email" -> "example@email.com")
      lazy val result = form.contactDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe ContactDetailsModel("Name", "11111111111", "example@email.com")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with no contact name" should {
      val map = Map("contactName" -> "", "telephone" -> "11111111111", "email" -> "example@email.com")
      lazy val result = form.contactDetailsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with no telephone number" should {
      val map = Map("contactName" -> "Name", "telephone" -> "", "email" -> "example@email.com")
      lazy val result = form.contactDetailsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with an invalid telephone number" should {
      val map = Map("contactName" -> "Name", "telephone" -> "1a1111111", "email" -> "example@email.com")
      lazy val result = form.contactDetailsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for an invalid telephone number" in {
        result.errors.head.message shouldBe Errors.errorTelephone
      }
    }

    "provided with no email" should {
      val map = Map("contactName" -> "Name", "telephone" -> "11111111111", "email" -> "")
      lazy val result = form.contactDetailsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorEmail
      }
    }

    "provided with an invalid email" should {
      val map = Map("contactName" -> "Name", "telephone" -> "11111111111", "email" -> "test")
      lazy val result = form.contactDetailsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorEmail
      }
    }
  }

}
