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
import config.AppConfig
import org.scalatest.mock.MockitoSugar
import org.jsoup.Jsoup
import forms.OrganisationForm._
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.inject.Injector

class OrganisationTypeViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar {

  val injector: Injector = fakeApplication.injector

  val appConfig: AppConfig = injector.instanceOf[AppConfig]

  implicit val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The organisation type view" should {

    lazy val view = views.html.errors.organisationType(appConfig, organisationForm)
    lazy val doc = Jsoup.parse(view.body)

    s"have a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    s"have a heading of ${messages.heading}" in {
      doc.select("h1").text shouldEqual messages.heading
    }

    "has a question that" should {

      s"has the text ${messages.question}" in {
        doc.select("p").get(1).text shouldEqual messages.question
      }

      "has a bold font" in {
        doc.select("p").get(1).hasClass("bold") shouldEqual true
      }
    }

    "have a set of radio buttons" which {

      lazy val buttons = doc.select("label")

      s"has a button for ${InvalidUserTypes.company}" in {
        buttons.get(0).text shouldEqual messages.company
      }

      s"has a button for ${InvalidUserTypes.charity}" in {
        buttons.get(1).text shouldEqual messages.charity
      }

      s"has a button for ${InvalidUserTypes.partnership}" in {
        buttons.get(2).text shouldEqual messages.partnership
      }

      s"has a button for ${InvalidUserTypes.trust}" in {
        buttons.get(3).text shouldEqual messages.trust
      }

      s"has a button for ${InvalidUserTypes.pensionTrust}" in {
        buttons.get(4).text shouldEqual messages.pensionTrust
      }
    }

    "has a form" which {

      "has a action too /capital-gains-subscription-frontend/subscribe/individual/organisation-type" in {
        doc.select("form").attr("action") shouldEqual "/capital-gains-subscription-frontend/subscribe/individual/organisation-type"
      }

      "has a action with type POST" in {
        doc.select("form").attr("method") shouldEqual "POST"
      }
    }

    "have a continue button that" should {

      lazy val continueButton = doc.select("button#continue-button")

      s"have the button text '${commonMessages.continue}'" in {
        continueButton.text shouldBe commonMessages.continue
      }

      "be of type submit" in {
        continueButton.attr("type") shouldBe "submit"
      }

      "have the class 'button'" in {
        continueButton.hasClass("button") shouldBe true
      }
    }
  }
}
