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

import forms.YesNoForm
import org.jsoup.Jsoup
import views.html.useRegisteredAddress
import data.MessageLookup.{Common, UseRegisteredAddress}
import models.CompanyAddressModel
import traits.ViewTestSpec

class UseRegisteredAddressViewSpec extends ViewTestSpec {

  "The useRegisteredAddress view when supplied with a valid form" should {
    lazy val form = new YesNoForm(messagesApi)
    val addressModel = CompanyAddressModel(
      Some("line1"),
      Some("line2"),
      Some("line3"),
      Some("line4"),
      Some("postCode"),
      Some("country")
    )
    lazy val view = useRegisteredAddress(appConfig, form.yesNoForm, addressModel)
    lazy val doc = Jsoup.parse(view.body)

    s"have a title of '${UseRegisteredAddress.title}'" in {
      doc.title() shouldBe UseRegisteredAddress.title
    }

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${UseRegisteredAddress.title}'" in {
        doc.select("h1").text() shouldBe UseRegisteredAddress.title
      }
    }

    "contain a list" which {
      lazy val list = doc.select("main ul")

      "has the first address line" in {
        list.select("li").get(0).text shouldBe "line1"
      }

      "has the second address line" in {
        list.select("li").get(1).text shouldBe "line2"
      }

      "has the third address line" in {
        list.select("li").get(2).text shouldBe "line3"
      }

      "has the fourth address line" in {
        list.select("li").get(3).text shouldBe "line4"
      }

      "has the postcode" in {
        list.select("li").get(4).text shouldBe "postCode"
      }

      "has the country" in {
        list.select("li").get(5).text shouldBe "country"
      }
    }

    "have a form" which {
      lazy val form = doc.body().select("form")

      "has a method of POST" in {
        form.attr("method") shouldBe "POST"
      }

      s"has an action of '${controllers.routes.CorrespondenceAddressConfirmController.submitCorrespondenceAddressConfirm().url}'" in {
        form.attr("action") shouldBe controllers.routes.CorrespondenceAddressConfirmController.submitCorrespondenceAddressConfirm().url
      }
    }

    "have a radio input" which {
      lazy val input = doc.select("fieldset")

      "has a class of 'inline form-group radio-list'" in {
        input.attr("class") shouldBe "inline form-group radio-list"
      }

      "has a legend with a class of 'heading-small'" in {
        input.select("legend").attr("class") shouldBe "heading-small"
      }

      s"has the legend '${UseRegisteredAddress.question}'" in {
        input.select("legend").text() shouldBe UseRegisteredAddress.question
      }

      "has a label for the yes option" which {
        lazy val yesLabel = input.select("label[for=response-yes]")

        "has the class 'block-label'" in {
          yesLabel.attr("class") shouldBe "block-label"
        }

        "has the text 'Yes'" in {
          yesLabel.text() shouldBe "Yes"
        }

        "has the name 'response'" in {
          yesLabel.select("input").attr("name") shouldBe "response"
        }

        "has the value 'Yes'" in {
          yesLabel.select("input").attr("value") shouldBe "Yes"
        }
      }

      "has a label for the no option" which {
        lazy val noLabel = input.select("label[for=response-no]")

        "has the class 'block-label'" in {
          noLabel.attr("class") shouldBe "block-label"
        }

        "has the text 'No'" in {
          noLabel.text() shouldBe "No"
        }

        "has the name 'response'" in {
          noLabel.select("input").attr("name") shouldBe "response"
        }

        "has the value 'No'" in {
          noLabel.select("input").attr("value") shouldBe "No"
        }
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

  "The useRegisteredAddress view with no optional data but a valid form" should {
    lazy val form = new YesNoForm(messagesApi)
    val addressModel = CompanyAddressModel(None, None, None, None, None, None)
    lazy val view = useRegisteredAddress(appConfig, form.yesNoForm, addressModel)
    lazy val doc = Jsoup.parse(view.body)

    "contain a list" which {
      lazy val list = doc.select("main ul")

      "has no elements" in {
        list.select("li").size() shouldBe 0
      }
    }
  }

  "The useRegisteredAddress view when supplied with a form with errors" should {

    lazy val form = new YesNoForm(messagesApi)
    lazy val map = Map("response" -> "")
    val addressModel = CompanyAddressModel(None, None, None, None, None, None)
    lazy val view = useRegisteredAddress(appConfig, form.yesNoForm.bind(map), addressModel)
    lazy val doc = Jsoup.parse(view.body).toString

    "display an error summary" in {
      doc should include("id=\"error-summary-display\"")
    }
  }
}
