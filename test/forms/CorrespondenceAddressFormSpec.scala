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

import data.MessageLookup.Errors
import models.CompanyAddressModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class CorrespondenceAddressFormSpec extends UnitSpec with OneAppPerSuite {

  "Creating a form" when {
    val form = app.injector.instanceOf[CorrespondenceAddressForm]

    "provided with a valid map with no optional values" should {
      val map = Map("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "",
        "addressLineFour" -> "", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val result = form.correspondenceAddressForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe CompanyAddressModel(Some("XX Fake Lane"), Some("Fake Town"), None, None, Some("Fakeland"), Some("XX22 1XX"))
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with a valid map with all optional values" should {
      val map = Map("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
        "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val result = form.correspondenceAddressForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe CompanyAddressModel(Some("XX Fake Lane"), Some("Fake Town"), Some("Fake City"),
          Some("Fake County"), Some("Fakeland"), Some("XX22 1XX"))
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with a invalid map without addressLineOne" should {
      val map = Map("addressLineOne" -> "", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
        "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val result = form.correspondenceAddressForm.bind(map)

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

    "provided with a invalid map without addressLineTwo" should {
      val map = Map("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "", "addressLineThree" -> "Fake City",
        "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val result = form.correspondenceAddressForm.bind(map)

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

    "provided with a invalid map without country" should {
      val map = Map("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
        "addressLineFour" -> "Fake County", "country" -> "", "postcode" -> "XX22 1XX")
      lazy val result = form.correspondenceAddressForm.bind(map)

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

    "provided with a invalid map without postcode" should {
      val map = Map("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
        "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "")
      lazy val result = form.correspondenceAddressForm.bind(map)

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
  }
}
