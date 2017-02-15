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
import assets.MessageLookup.UserDetails
import assets.MessageLookup.Common
import config.AppConfig
import forms.UserFactsForm
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import uk.gov.hmrc.play.test.UnitSpec
import views.html.userDetails

class UserDetailsViewSpec extends UnitSpec with OneAppPerSuite with FakeRequestHelper with I18nSupport {

  lazy val injector: Injector = app.injector
  lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The User Details view when supplied with a form with no errors" should {
    lazy val form = new UserFactsForm(messagesApi)
    lazy val view = userDetails(appConfig, form.fullDetailsForm)
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${UserDetails.title}'" in {
        doc.select("h1").text() shouldBe UserDetails.title
      }
    }

    "have a form" which {
      lazy val form = doc.body().select("form")

      "has a method of POST" in {
        form.attr("method") shouldBe "POST"
      }

      s"has an action of '${controllers.routes.UserDetailsController.submitUserDetails().url}'" in {
        form.attr("action") shouldBe controllers.routes.UserDetailsController.submitUserDetails().url
      }
    }

    "has an input for first name" which {
      lazy val label = doc.body().select("label[for=firstName]")
      lazy val input = label.select("input#firstName")

      s"has the text '${UserDetails.firstName}'" in {
        label.text() shouldBe UserDetails.firstName
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

    "has an input for last name" which {
      lazy val label = doc.body().select("label[for=lastName]")
      lazy val input = label.select("input#lastName")

      s"has the text '${UserDetails.lastName}'" in {
        label.text() shouldBe UserDetails.lastName
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

    "have a secondary header" should {
      lazy val secondHeader = doc.body().select("h2").first()

      "have a class of heading-medium" in {
        secondHeader.attr("class") shouldBe "heading-medium"
      }

      s"have the text '${UserDetails.secondHeader}'" in {
        secondHeader.text() shouldBe UserDetails.secondHeader
      }
    }

    "has an input for the address line one" which {
      lazy val label = doc.body().select("label[for=addressLineOne]")
      lazy val input = label.select("input#addressLineOne")

      s"has the text '${UserDetails.addressLineOne}'" in {
        label.text() shouldBe UserDetails.addressLineOne
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

    "has an input for the address line two" which {
      lazy val label = doc.body().select("label[for=addressLineTwo]")
      lazy val input = label.select("input#addressLineTwo")

      s"has the text '${UserDetails.addressLineTwo}'" in {
        label.text() shouldBe UserDetails.addressLineTwo
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a span with a class of visuallyhidden" in {
        label.select("span").attr("class") shouldBe "visuallyhidden"
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }

      "has a class of 'shim input grid-1-2'" in {
        input.attr("class") shouldBe "shim input grid-1-2"
      }
    }

    "has an input for town or city" which {
      lazy val label = doc.body().select("label[for=townOrCity]")
      lazy val input = label.select("input#townOrCity")

      s"has the text '${UserDetails.townOrCity}'" in {
        label.text() shouldBe UserDetails.townOrCity
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

    "has an input for county" which {
      lazy val label = doc.body().select("label[for=county]")
      lazy val input = label.select("input#county")

      s"has the text '${UserDetails.county}'" in {
        label.text() shouldBe UserDetails.county
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
      lazy val label = doc.body().select("label[for=postCode]")
      lazy val input = label.select("input#postCode")

      s"has the text '${UserDetails.postCode}'" in {
        label.text() shouldBe UserDetails.postCode
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

    "has an input for country" which {
      lazy val label = doc.body().select("label[for=country]")
      lazy val input = label.select("input#country")

      s"has the text '${UserDetails.country}'" in {
        label.text() shouldBe UserDetails.country
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

  "The User Details view when supplied with a form with errors" should {

    lazy val form = new UserFactsForm(messagesApi)
    lazy val map = Map("firstName" -> "", "lastName" -> "")
    lazy val view = userDetails(appConfig, form.fullDetailsForm.bind(map))
    lazy val doc = Jsoup.parse(view.body).toString

    "display an error summary" in {
      doc should include("id=\"error-summary-display\"")
    }
  }
}
