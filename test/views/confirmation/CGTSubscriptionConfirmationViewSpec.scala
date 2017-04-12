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

///*
// * Copyright 2017 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package views.confirmation
//
//import data.MessageLookup.{CGTSubscriptionConfirm => messages}
//import data.MessageLookup.{Common => commonMessages}
//import config.AppConfig
//import org.jsoup._
//import org.scalatest.mock.MockitoSugar
//import views.html.confirmation.cgtSubscriptionConfirmation
//import play.api.i18n.{I18nSupport, MessagesApi}
//import play.api.inject.Injector
//import traits.FakeRequestHelper
//import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
//
//class CGTSubscriptionConfirmationViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar with I18nSupport {
//
//  val injector: Injector = fakeApplication.injector
//
//  val appConfig: AppConfig = injector.instanceOf[AppConfig]
//
//  implicit val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
//
//  "The cgtSubscriptionConfirmationView" should {
//
//    lazy val view = cgtSubscriptionConfirmation(appConfig, "Generic CGT reference")
//    lazy val doc = Jsoup.parse(view.body)
//
//    s"display a title of ${messages.title}" in {
//      doc.title shouldEqual messages.title
//    }
//
//    "have the heading" which {
//
//      s"should have the text ${messages.title}" in {
//        doc.select("h1").text shouldEqual messages.title
//      }
//
//      "should have the class visually hidden" in {
//        doc.select("h1").hasClass("visuallyhidden") shouldEqual true
//      }
//    }
//
//    "have the content" which {
//
//      "should have a div" which {
//
//        lazy val greenBanner = doc.select("div #confirmation-banner")
//
//        "has the class transaction-banner--complete" in {
//          greenBanner.hasClass("transaction-banner--complete") shouldEqual true
//        }
//
//        s"has the first paragraph" which {
//
//          s"has text ${messages.title}" in {
//            greenBanner.select("p").get(0).text shouldEqual messages.title
//          }
//
//          "has the class bold-large" in {
//            greenBanner.select("p").get(0).hasClass("bold-large") shouldEqual true
//          }
//        }
//
//        "has a second paragraph" which {
//
//          "has the text 'Generic CGT reference'" in {
//            greenBanner.select("p").get(1).text shouldEqual "Generic CGT reference"
//          }
//
//          "has the class heading-medium" in {
//            greenBanner.select("p").get(1).hasClass("heading-medium") shouldEqual true
//          }
//        }
//      }
//
//      "has a div" which {
//
//        lazy val content = doc.select("div #instruction-information")
//
//        "has the class form-group" in {
//          content.hasClass("form-group") shouldEqual true
//        }
//
//        "has content" which {
//
//          "has a first paragraph that" should {
//
//            "have the class lede" in {
//              content.select("p").get(0).hasClass("lede") shouldEqual true
//            }
//
//            s"have the text ${messages.writeDown}" in {
//              content.select("p").get(0).text shouldEqual messages.writeDown
//            }
//          }
//
//          s"has a second paragraph with the text ${messages.forgetGGID}" in {
//            content.select("p").get(1).text shouldEqual messages.forgetGGID
//          }
//
//          "has a third paragraph that" should {
//
//            "have the class heading-medium" in {
//              content.select("p").get(2).hasClass("heading-medium") shouldEqual true
//            }
//
//            s"have the text ${messages.whatNext}" in {
//              content.select("p").get(2).text shouldEqual messages.whatNext
//            }
//          }
//
//          s"has a fourth paragraph with the text ${messages.whatNextContent}" in {
//            content.select("p").get(3).text shouldEqual messages.whatNextContent
//          }
//
//          "has a fifth paragraph that" should {
//
//            "have the class indent" in {
//              content.select("p").get(4).hasClass("indent") shouldEqual true
//            }
//
//            s"have the text ${messages.figuresReady}" in {
//              content.select("p").get(4).text shouldEqual messages.figuresReady
//            }
//          }
//        }
//      }
//    }
//
//    "have a form that" should {
//
//      lazy val form = doc.select("form")
//
//      "have the action /capital-gains-tax/subscription/confirmation" in {
//        form.attr("action") shouldEqual "/capital-gains-tax/subscription/confirmation"
//      }
//
//      "have the action type POST" in {
//        form.attr("method") shouldEqual "POST"
//      }
//
//      "have a continue button that" should {
//
//        lazy val continueButton = doc.select("button#continue-button")
//
//        s"have the button text '${commonMessages.continue}'" in {
//          continueButton.text shouldBe commonMessages.continue
//        }
//
//        "be of type submit" in {
//          continueButton.attr("type") shouldBe "submit"
//        }
//
//        "have the class 'button'" in {
//          continueButton.hasClass("button") shouldBe true
//        }
//      }
//    }
//  }
//}
//
//
