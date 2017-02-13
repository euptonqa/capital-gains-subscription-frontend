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

package views.helpers

import assets.MessageLookup.{ErrorSummary => messages}
import assets.MessageLookup.{Errors => errorMessages}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.html.helpers.{errorSummary => view}
import assets.TestForm._
import org.jsoup.Jsoup
import play.api.i18n.{I18nSupport, MessagesApi}

class ErrorSummaryViewSpec extends UnitSpec with WithFakeApplication with I18nSupport {

  implicit val messagesApi: MessagesApi = fakeApplication.injector.instanceOf[MessagesApi]

  "Calling the error summary helper" when {

    "the form supplied has no errors" should {

      lazy val form = testForm
      lazy val partial = view(form, "page")

      "not display the error summary element" in {
        Jsoup.parse(partial.body).toString should not include "id=\"error-summary-display\""
      }
    }

    //The reason for specifying this single error is that the error summary can also raise a
    //generalised error when the form fails to bind to the model as a whole, rather than the elements.
    "the form supplied has specific binding error" should {

      lazy val form = testForm.bind(Map("response" -> "", "anotherField" -> ""))
      lazy val partial = view(form, "page")
      lazy val doc = Jsoup.parse(partial.body)

      "display the error summary" which {

        "has an outer div that" should {

          "have the class 'flash'" in {
            doc.select("div").get(0).hasClass("flash") shouldEqual true
          }

          "have the class 'error-summary'" in {
            doc.select("div").get(0).hasClass("error-summary") shouldEqual true
          }

          "have the class 'error-summary--show'" in {
            doc.select("div").get(0).hasClass("error-summary--show") shouldEqual true
          }

          "have the id 'error-summary-display'" in {
            doc.select("div").get(0).id() shouldEqual "error-summary-display"
          }

          "have the role 'alert'" in {
            doc.select("div").get(0).attr("role") shouldEqual "alert"
          }

          "have the aria-labelledby attribute 'error-summary-heading'" in {
            doc.select("div").get(0).attr("aria-labelledby") shouldEqual "error-summary-heading"
          }

          "have the tab index of '-1'" in {
            doc.select("div").get(0).attr("tabindex") shouldEqual "-1"
          }

          "has children elements" which {

            "include a h2 tag that" should {

              "have the id 'error-summary-heading'" in {
                doc.select("h2").get(0).id() shouldEqual "error-summary-heading"
              }

              "have the class 'h3-heading'" in {
                doc.select("h2").get(0).hasClass("h3-heading") shouldEqual true
              }

              "has the text" in {
                doc.select("h2").text shouldEqual messages.errorSummaryHeading
              }
            }

            "include a ul element" which {

              "has the class 'js-error-summary-messages'" in {
                doc.select("ul").get(0).hasClass("js-error-summary-messages") shouldEqual true
              }

              "has a child 'li' element that" should {

                "have the role 'tooltip'" in {
                  doc.select("li").get(0).attr("role") shouldEqual "tooltip"
                }

                "have the data-journey attribute of 'page:response:'" in {
                  doc.select("li").get(0).attr("data-journey") shouldEqual "page:error:response"
                }

                "contain a child 'a' element" which {

                  "should have the href '#response'" in {
                    doc.select("a").get(0).attr("href") shouldEqual "#response"
                  }

                  "should have the id 'response-error-summary'" in {
                    doc.select("a").get(0).id() shouldEqual "response-error-summary"
                  }

                  "should have the data-focus 'response'" in {
                    doc.select("a").get(0).attr("data-focuses") shouldEqual "response"
                  }

                  s"should have the error message ${errorMessages.dummyError}" in {
                    doc.select("a").get(0).text shouldEqual errorMessages.dummyError
                  }
                }
              }
            }
          }
        }
      }
    }

    "the form was provided with an invalid map" should {

      lazy val form = testForm.bind(Map("response" -> "Something", "anotherField" -> "fsad"))
      lazy val partial = view(form, "page", "dummyKey")
      lazy val doc = Jsoup.parse(partial.body)

      "raise a general error" which {

        "changes the 'li' element so that it" should {

          "have the role 'tooltip'" in {
            doc.select("li").get(0).attr("role") shouldEqual "tooltip"
          }

          "have the data-journey attribute of 'page:response:'" in {
            doc.select("li").get(0).attr("data-journey") shouldEqual "page:error:dummyKey"
          }

          "contain a child 'a' element" which {

            "should have the href '#dummyKey'" in {
              doc.select("a").get(0).attr("href") shouldEqual "#dummyKey"
            }

            "should have the id 'dummyKey-error-summary'" in {
              doc.select("a").get(0).id() shouldEqual "dummyKey-error-summary"
            }

            "should have the data-focus 'dummyKey'" in {
              doc.select("a").get(0).attr("data-focuses") shouldEqual "dummyKey"
            }

            s"should have the error message ${errorMessages.dummyError}" in {
              doc.select("a").get(0).text shouldEqual errorMessages.dummyError
            }
          }
        }
      }
    }
  }
}
