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

import data.MessageLookup.{Common, ContactDetails => messages}
import forms.ContactDetailsForm
import org.jsoup.Jsoup
import traits.ViewTestSpec
import views.html.contactDetails

class ContactDetailsViewSpec extends ViewTestSpec {

  lazy val form = injector.instanceOf[ContactDetailsForm]

  "The contact details view" should {
    lazy val view = contactDetails(appConfig, form.contactDetailsForm)
    lazy val doc = Jsoup.parse(view.body)

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${messages.title}'" in {
        doc.select("h1").text() shouldBe messages.title
      }
    }

    "have a form" which {
      lazy val form = doc.body().select("form")

      "has a method of POST" in {
        form.attr("method") shouldBe "POST"
      }

      s"has an action of '${controllers.routes.ContactDetailsController.submitContactDetails().url}'" in {
        form.attr("action") shouldBe controllers.routes.ContactDetailsController.submitContactDetails().url
      }
    }

    "has some text" which {
      lazy val text = doc.select("main p").get(1)

      "has a class of 'lede'" in {
        text.attr("class") shouldBe "lede"
      }

      s"has the text '${messages.text}'" in {
        text.text() shouldBe messages.text
      }
    }

    "has an input for contact name" which {
      lazy val label = doc.body().select("label[for=contactName]")
      lazy val input = label.select("input#contactName")

      s"has the text '${messages.contactName}'" in {
        label.text() shouldBe messages.contactName
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

    "has an input for telephone" which {
      lazy val label = doc.body().select("label[for=telephone]")
      lazy val input = label.select("input#telephone")

      s"has the text '${messages.telephone}'" in {
        label.text() shouldBe messages.telephone
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

    "has an input for email" which {
      lazy val label = doc.body().select("label[for=email]")
      lazy val input = label.select("input#email")

      s"has the text '${messages.email}'" in {
        label.text() shouldBe messages.email
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
}
