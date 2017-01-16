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

package views

import assets.FakeRequestHelper
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import common.Constants.InvalidUserTypes
import assets.MessageLookup.{OrganisationType => messages}
import assets.MessageLookup.{Common => commonMessages}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.jsoup.Jsoup

class OrganisationTypeViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar {

  "The organisation type view" should {

//    lazy val doc = Jsoup.parse(bodyOf(organisationType))

    s"have a title of ${messages.title}" in {

    }

    s"have a heading of ${messages.heading}" in {

    }

    "has a question that" should {

      s"has the text ${messages.question}" in {

      }

      "has a bold font" in {

      }
    }

    "have a set of radio buttons" which {

      s"has a button for ${InvalidUserTypes.company}" in {

      }

      s"has a button for ${InvalidUserTypes.charity}" in {

      }

      s"has a button for ${InvalidUserTypes.partnership}" in {

      }

      s"has a button for ${InvalidUserTypes.trust}" in {

      }

      s"has a button for ${InvalidUserTypes.pensionTrust}" in {

      }
    }

    "has a form field" which {

      "has type ...." in {

      }

      "has a action too ...." in {

      }

      "has a action with type POST" in {

      }

    }

//    "have a continue button that" should {
//
//      lazy val continueButton = doc.select("button#continue-button")
//
//      s"have the button text '${commonMessages.continue}'" in {
//        continueButton.text shouldBe commonMessages.continue
//      }
//
//      "be of type submit" in {
//        continueButton.attr("type") shouldBe "submit"
//      }
//
//      "have the class 'button'" in {
//        continueButton.hasClass("button") shouldBe true
//      }
//    }
  }
}
