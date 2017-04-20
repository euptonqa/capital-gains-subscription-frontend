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

package common

import uk.gov.hmrc.play.test.UnitSpec

class FormValidationSpec extends UnitSpec {

  "Calling .nonEmptyCheck" should {

    "when called with an empty string return false" in {
      FormValidation.nonEmptyCheck("") shouldEqual false
    }

    "when called with a string that has the value '~' return true" in {
      FormValidation.nonEmptyCheck("~") shouldEqual true
    }

    "when called with a string that has the value 'fews' return true" in {
      FormValidation.nonEmptyCheck("fews") shouldEqual true
    }

    "when called with a string that has the value '@' return true" in {
      FormValidation.nonEmptyCheck("@") shouldEqual true
    }
  }

  "Calling .textToOptional" should {

    "when called with an empty string return None" in {
      FormValidation.textToOptional("") shouldEqual None
    }

    "when called with a string of 'qwerty' return Some('qwerty')" in {
      FormValidation.textToOptional("qwerty") shouldEqual Some("qwerty")
    }

    "when called with a string of '~' return Some('~')" in {
      FormValidation.textToOptional("~") shouldEqual Some("~")
    }
  }

  "Calling .optionalToText" should {

    "when called with None return an empty string" in {
      FormValidation.optionalToText(None) shouldEqual ""
    }

    "when called with an Option of Some('#') return '#'" in {
      FormValidation.optionalToText(Some("#")) shouldEqual "#"
    }

    "when called with an Option of Some('~') return '~'" in {
      FormValidation.optionalToText(Some("~")) shouldEqual "~"
    }
  }

  "Calling .postcodeCheck" when {

    "called with no postcode and a non-UK country" should {
      lazy val result = FormValidation.postcodeCheck(None, "DE")

      "return a true" in {
        result shouldBe true
      }
    }

    "called with a postcode and a non-UK country" should {
      lazy val result = FormValidation.postcodeCheck(Some("XX11 1XX"), "DE")

      "return a true" in {
        result shouldBe true
      }
    }

    "called with no postcode and a country of UK" should {
      lazy val result = FormValidation.postcodeCheck(None, "GB")

      "return a false" in {
        result shouldBe false
      }
    }

    "called with a postcode and a country of UK" should {
      lazy val result = FormValidation.postcodeCheck(Some("XX11 1XX"), "GB")

      "return a true" in {
        result shouldBe true
      }
    }
  }
}
