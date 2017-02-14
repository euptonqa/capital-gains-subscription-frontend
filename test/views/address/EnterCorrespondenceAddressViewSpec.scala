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

package views.address

import assets.FakeRequestHelper
import assets.MessageLookup.{Common, EnterCorrespondenceAddress}
import config.AppConfig
import forms.CorrespondenceAddressForm
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import uk.gov.hmrc.play.test.UnitSpec
import views.html.address.enterCorrespondenceAddress

class EnterCorrespondenceAddressViewSpec extends UnitSpec with OneAppPerSuite with FakeRequestHelper with I18nSupport {

  lazy val injector: Injector = app.injector
  lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The Enter Correspondence Address view with a form with no errors" should {
    lazy val form = new CorrespondenceAddressForm(messagesApi)
    lazy val view = enterCorrespondenceAddress(appConfig, form.correspondenceAddressForm)
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${EnterCorrespondenceAddress.title}'" in {
        doc.select("h1").text() shouldBe EnterCorrespondenceAddress.title
      }
    }

    "have a form" which {
      lazy val form = doc.select("form")

      "has a method of POST" in {
        form.attr("method") shouldBe "POST"
      }

      s"has an action of '${controllers.routes.EnterCorrespondenceAddressController.submitCorrespondenceAddress().url}'" in {
        form.attr("action") shouldBe controllers.routes.EnterCorrespondenceAddressController.submitCorrespondenceAddress().url
      }
    }

    "has an input for address line one" which {
      lazy val label = doc.select("label[for=addressLineOne]")
      lazy val input = label.select("input#addressLineOne")

      s"has the text '${EnterCorrespondenceAddress.addressLineOne}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.addressLineOne
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for addressLineTwo" which {
      lazy val label = doc.select("label[for=addressLineTwo]")
      lazy val input = label.select("input#addressLineTwo")

      s"has the text '${EnterCorrespondenceAddress.addressLineTwo}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.addressLineTwo
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for the addressLineThree" which {
      lazy val label = doc.select("label[for=addressLineThree]")
      lazy val input = label.select("input#addressLineThree")

      s"has the text '${EnterCorrespondenceAddress.addressLineThree}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.addressLineThree
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for the addressLineFour" which {
      lazy val label = doc.select("label[for=addressLineFour]")
      lazy val input = label.select("input#addressLineFour")

      s"has the text '${EnterCorrespondenceAddress.addressLineFour}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.addressLineFour
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for country" which {
      lazy val label = doc.select("label[for=country]")
      lazy val input = label.select("input#country")

      s"has the text '${EnterCorrespondenceAddress.country}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.country
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for postcode" which {
      lazy val label = doc.select("label[for=postcode]")
      lazy val input = label.select("input#postcode")

      s"has the text '${EnterCorrespondenceAddress.postcode}'" in {
        label.text() shouldBe EnterCorrespondenceAddress.postcode
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-4'" in {
        input.attr("class") shouldBe "shim input grid-1-4"
      }
    }

    "have a button" which {
      lazy val button = doc.select("button")

      "has the text 'Continue'" in {
        button.text() shouldBe Common.continue
      }

      "has the class 'button'" in {
        button.attr("class") shouldBe "button"
      }

      "has the type 'submit'" in {
        button.attr("type") shouldBe "submit"
      }

      "has the id 'continue-button'" in {
        button.attr("id") shouldBe "continue-button"
      }
    }
  }

  "The Enter Correspondence Address view with a form with errors" should {
    lazy val form = new CorrespondenceAddressForm(messagesApi)
    lazy val map = Map("addressLineOne" -> "Something", "addressLineTwo" -> "Something else")
    lazy val view = enterCorrespondenceAddress(appConfig, form.correspondenceAddressForm.bind(map))
    lazy val doc = Jsoup.parse(view.body).toString

    "display an error summary" in {
      doc should include("id=\"error-summary-display\"")
    }
  }
}
