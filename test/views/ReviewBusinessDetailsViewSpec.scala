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

import assets.{FakeRequestHelper, MessageLookup}
import config.AppConfig
import models.{CompanyAddressModel, ContactDetailsModel}
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import uk.gov.hmrc.play.test.UnitSpec
import views.html.reviewBusinessDetails
import assets.MessageLookup.{ReviewBusinessDetails => messages}


class ReviewBusinessDetailsViewSpec extends UnitSpec with OneAppPerSuite with FakeRequestHelper with I18nSupport {
  lazy val injector: Injector = app.injector
  lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The ReviewBusinessDetailsView" should {

    val registeredModel = CompanyAddressModel(Some("hello"), Some("hello"), None, None, None, None)
    val contactModel = CompanyAddressModel(Some("hello"), Some("hello"), None, None, None, None)
    val detailsModel = ContactDetailsModel("name", "telephone", "email")
    lazy val view = reviewBusinessDetails(appConfig, registeredModel, contactModel, "business name", detailsModel)
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {
      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${messages.title}" in {
        doc.select("h1").text() shouldBe messages.title
      }
    }

    "have a h2" which {
      "has the class 'heading'" in {
        doc.select("h2").attr("class") shouldBe "heading"
      }

      "has the inline style 'font-weight:normal'" in {
        doc.select("h2").attr("style") shouldBe "font-weight:normal"
      }
    }

    "has a div of class form-group" which {
      lazy val element = doc.select("div.form-group")
      "has a table that" should {
        lazy val table = element.select("table")

        "that should have a table row" which {
          lazy val row = table.select("tr:nth-of-type(1)")
          "has an header with an inline style of 'width:60%'" in {
            row.select("th").attr("style") shouldBe "width:60%"
          }
          "has a td with the text 'name''" in {
            row.select("td:nth-of-type(1)").text() shouldBe "business name"
          }
          "has an empty td" which {
            lazy val tdTwo = row.select("td:nth-of-type(2)")
            "has empty html" in {
              tdTwo.html() shouldBe ""
            }
          }
        }
        "has a secondary table row" which {
            lazy val row = table.select("tr:nth-of-type(2)")
            "has a table header" which {
              lazy val tHeader = row.select("th")
              "has an inline style of 'vertical-align: top'" in {
                tHeader.attr("style") shouldBe "vertical-align: top"
              }
              s"with a title ${messages.registeredAddress}" in {
                tHeader.text() shouldBe messages.registeredAddress
              }
            "has a td with the relevant address details found in the registeredAddress model" which {
              lazy val tdOne = row.select("td:nth-of-type(1)")
              "with the innerHtml" in {
                tdOne.html() shouldBe registeredModel.addressLine1.get + "<br> " +
                  registeredModel.addressLine2.get + "<br>"
              }
            }
          }
        }
        "has a tertiary table row" which {
          lazy val row = table.select("tr:nth-of-type(3)")
          "has a table header" which {
            lazy val tHeader = row.select("th")
            "has an inline style of 'vertical-align: top'" in {
              tHeader.attr("style") shouldBe "vertical-align: top"
            }
            s"with a title ${messages.correspondenceAddress}" in {
              tHeader.text() shouldBe messages.correspondenceAddress
            }
          }
          "has a td with the relevant address details found in the correspondenceAddress model" which {
            lazy val tdOne = row.select("td:nth-of-type(1)")
            "with the innerHtml" in {
              tdOne.html() shouldBe registeredModel.addressLine1.get + "<br> " +
                registeredModel.addressLine2.get + "<br>"
            }
            s"has a td for ${MessageLookup.Common.change}" in {
              lazy val tdTwo = row.select("td:nth-of-type(2)")
              tdTwo.text() shouldBe MessageLookup.Common.change
            }
          }
        }

        "have a fourth table row" which {
          //TODO: needs further updating when view updated to take in CGT contact details
          lazy val row = table.select("tr:nth-of-type(4)")
          "has a table header" which {
            lazy val tHeader = row.select("th")
            "has an inline style of 'vertical-align: top'" in {
              tHeader.attr("style") shouldBe "vertical-align: top"
            }
            s"with a title ${messages.contactDetails}" in {
              tHeader.text() shouldBe messages.contactDetails
            }
          }
          "has a td for CGT contact details" in {

          }

          "has a td for Change" in {
            lazy val tdTwo = row.select("td:nth-of-type(2)")
            tdTwo.text() shouldBe MessageLookup.Common.change
          }
        }

        "have a button" which {
          lazy val button = doc.select("input")
          s"has the text ${messages.registerConfirm}" in {
            button.attr("value") shouldBe messages.registerConfirm
          }

          "has the class 'button'" in {
            button.attr("class") shouldBe "button"
          }

          "has the type 'submit'" in {
            button.attr("type") shouldBe "submit"
          }
        }
      }
    }
  }
}