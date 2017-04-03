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

import play.api.i18n.MessagesApi
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class FormValidationSpec extends UnitSpec with WithFakeApplication{

  implicit val messagesApi: MessagesApi = fakeApplication.injector.instanceOf[MessagesApi]
  val formValidation = new FormValidation(messagesApi)

  "Calling .nonEmptyCheck" should {

    "when called with an empty string return false" in {
      formValidation.nonEmptyCheck("") shouldEqual false
    }

    "when called with a string that has the value '~' return true" in {
      formValidation.nonEmptyCheck("~") shouldEqual true
    }

    "when called with a string that has the value 'fews' return true" in {
      formValidation.nonEmptyCheck("fews") shouldEqual true
    }

    "when called with a string that has the value '@' return true" in {
      formValidation.nonEmptyCheck("@") shouldEqual true
    }
  }

  "Calling .textToOptional" should {

    "when called with an empty string return None" in {
      formValidation.textToOptional("") shouldEqual None
    }

    "when called with a string of 'qwerty' return Some('querty')" in {
      formValidation.textToOptional("qwerty") shouldEqual Some("qwerty")
    }

    "when called with a string of '~' return Some('~')" in {
      formValidation.textToOptional("~") shouldEqual Some("~")
    }
  }

  "Calling .optionalToText" should {

    "when called with None return an empty string" in {
      formValidation.optionalToText(None) shouldEqual ""
    }

    "when called with an Option of Some('#') return '#'" in {
      formValidation.optionalToText(Some("#")) shouldEqual "#"
    }

    "when called with an Option of Some('~') return '~'" in {
      formValidation.optionalToText(Some("~")) shouldEqual "~"
    }
  }

}
