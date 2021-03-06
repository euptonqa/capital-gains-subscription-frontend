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

import models.UserFactsModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import data.MessageLookup.Errors

class UserFactsFormSpec extends UnitSpec with OneAppPerSuite {

  "Creating a form" when {
    val form = app.injector.instanceOf[UserFactsForm]

    "provided with a valid map with no optional values" should {
      val map = Map("firstName" -> "Bob", "lastName" -> "Smith", "addressLineOne" -> "XX Fake Lane","addressLineTwo" -> "Fakeville",
        "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "Fakeland")
      lazy val result = form.userFactsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe UserFactsModel("Bob", "Smith", "XX Fake Lane", "Fakeville", None, None, None, "Fakeland")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with a valid map with all optional values" should {
      val map = Map("firstName" -> "Bob", "lastName" -> "Smith", "addressLineOne" -> "XX", "addressLineTwo" -> "Fake Lane",
        "townOrCity" -> "Fakeville", "county" -> "Fake", "postCode" -> "XX22 1XX", "country" -> "Fakeland")
      lazy val result = form.userFactsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe UserFactsModel("Bob", "Smith", "XX", "Fake Lane", Some("Fakeville"), Some("Fake"), Some("XX22 1XX"), "Fakeland")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with an invalid map with no firstName" should {
      val map = Map("firstName" -> "", "lastName" -> "Smith", "addressLineOne" -> "XX Fake Lane","addressLineTwo" -> "Fakeville",
        "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "Fakeland")
      lazy val result = form.userFactsForm.bind(map)

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

    "provided with an invalid map with no lastName" should {
      val map = Map("firstName" -> "Bob", "lastName" -> "", "addressLineOne" -> "XX Fake Lane","addressLineTwo" -> "Fakeville",
        "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "Fakeland")
      lazy val result = form.userFactsForm.bind(map)

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

    "provided with an invalid map with no first address line" should {
      val map = Map("firstName" -> "Bob", "lastName" -> "Smith", "addressLineOne" -> "","addressLineTwo" -> "Fakeville",
        "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "Fakeland")
      lazy val result = form.userFactsForm.bind(map)

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

    "provided with an invalid map with no postcode for a country of UK" should {
      val map = Map("firstName" -> "Bob", "lastName" -> "Smith", "addressLineOne" -> "68","addressLineTwo" -> "Fakeville",
        "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "GB")
      lazy val result = form.userFactsForm.bind(map)

      "return an invalid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorPostcode
      }
    }
  }
}
