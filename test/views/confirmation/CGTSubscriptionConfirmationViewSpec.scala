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

package views.confirmation

import data.MessageLookup.{CGTSubscriptionConfirm => messages}
import data.MessageLookup.{Common => commonMessages}
import config.AppConfig
import org.jsoup._
import org.scalatest.mock.MockitoSugar
import views.html.confirmation.cgtSubscriptionConfirmation
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import traits.FakeRequestHelper
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CGTSubscriptionConfirmationViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar with I18nSupport {

  val injector: Injector = fakeApplication.injector

  val appConfig: AppConfig = injector.instanceOf[AppConfig]

  implicit val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The cgtSubscriptionConfirmationView" should {

    lazy val view = cgtSubscriptionConfirmation(appConfig, "Generic CGT reference",
      controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionCompany())
    lazy val doc = Jsoup.parse(view.body)

    "for a resident individual" should {

      lazy val view = cgtSubscriptionConfirmation(appConfig, "Generic CGT reference",
        controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionResidentIndv())
      lazy val document = Jsoup.parse(view.body)

      lazy val form = document.select("form")


      s"have the action ${controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionResidentIndv().url} "in {
        form.attr("action") shouldEqual controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionResidentIndv().url
      }

      "have the action type POST" in {
        form.attr("method") shouldEqual "POST"
      }
    }

    "for a non-resident individual" should {

      lazy val view = cgtSubscriptionConfirmation(appConfig, "Generic CGT reference",
        controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionNonResIndv())
      lazy val document = Jsoup.parse(view.body)

      lazy val form = document.select("form")


      s"have the action ${controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionNonResIndv().url} "in {
        form.attr("action") shouldEqual controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionNonResIndv().url
      }

      "have the action type POST" in {
        form.attr("method") shouldEqual "POST"
      }
    }

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    "have the heading" which {

      s"should have the text ${messages.title}" in {
        doc.select("h1").text shouldEqual messages.title
      }

      "should have the class visually hidden" in {
        doc.select("h1").hasClass("visuallyhidden") shouldEqual true
      }
    }

    "have the content" which {

      "should have a div" which {

        lazy val greenBanner = doc.select("div #confirmation-banner")

        "has the class transaction-banner--complete" in {
          greenBanner.hasClass("transaction-banner--complete") shouldEqual true
        }

        s"has the first paragraph" which {

          lazy val p = greenBanner.select("p")

          "has the class confirm-padding-below" in {
            p.hasClass("confirm-padding-below") shouldEqual true
          }

          "has a strong tag" which {

            lazy val strong = p.select("strong")

            s"has text ${messages.title}" in {
              strong.select("strong").text shouldEqual messages.title
            }

            "has the class bold-large" in {
              strong.hasClass("bold-large") shouldEqual true
            }
          }
        }

        "has a strong tag" which {

          lazy val strong = greenBanner.select("strong").get(1)

          s"has text 'Generic CGT reference'" in {
            strong.select("strong").text shouldEqual "Generic CGT reference"
          }

          "has the class heading-medium" in {
            strong.hasClass("heading-medium") shouldEqual true
          }
        }
      }

      "has a div" which {

        lazy val content = doc.select("div #write-this-down")

        "has the class form-group instruction-margin" in {
          content.attr("class") shouldEqual "form-group instruction-margin"
        }

        "has content" which {

          "has a span that" should {

            "have the class lede" in {
              content.select("span").get(0).hasClass("lede") shouldEqual true
            }

            s"have the text ${messages.writeDown}" in {
              content.select("span").get(0).text shouldEqual messages.writeDown
            }
          }

          s"has a paragraph with the text ${messages.forgetGGID}" in {
            content.select("p").text shouldEqual messages.forgetGGID
          }
        }
      }

      "has a second div" which {

        lazy val content = doc.select("div #what-happens-next")

        "has the class form-group instruction-margin" in {
          content.attr("class") shouldEqual "form-group instruction-margin"
        }

        "has a first paragraph" which {

          lazy val p = content.select("p").get(0)

          "has a strong tag" which {

            "have the class heading-medium" in {
              p.select("strong").hasClass("heading-medium") shouldEqual true
            }

            s"have the text ${messages.whatNext}" in {
              p.select("strong").text shouldEqual messages.whatNext
            }
          }
        }

        s"has a second paragraph with the text ${messages.whatNextContent}" in {
          content.select("p").get(1).text shouldEqual messages.whatNextContent
        }

        "has a third paragraph that" should {

          lazy val p = content.select("p").get(2)

          "have the class indent" in {
            p.hasClass("indent") shouldEqual true
          }

          s"have the text ${messages.figuresReady}" in {
            p.text shouldEqual messages.figuresReady
          }
        }
      }
    }

    "have a form that" should {

      lazy val form = doc.select("form")

      s"have the action ${controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionCompany().url}" in {
        form.attr("action") shouldEqual controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscriptionCompany().url
      }

      "have the action type POST" in {
        form.attr("method") shouldEqual "POST"
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
}


