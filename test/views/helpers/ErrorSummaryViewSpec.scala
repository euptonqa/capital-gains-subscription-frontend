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

          lazy val div = doc.select("div").get(0)

          "have the class 'flash'" in {
            div.hasClass("flash") shouldEqual true
          }

          "have the class 'error-summary'" in {
            div.hasClass("error-summary") shouldEqual true
          }

          "have the class 'error-summary--show'" in {
            div.hasClass("error-summary--show") shouldEqual true
          }

          "have the id 'error-summary-display'" in {
            div.id() shouldEqual "error-summary-display"
          }

          "have the role 'alert'" in {
            div.attr("role") shouldEqual "alert"
          }

          "have the aria-labelledby attribute 'error-summary-heading'" in {
            div.attr("aria-labelledby") shouldEqual "error-summary-heading"
          }

          "have the tab index of '-1'" in {
            div.attr("tabindex") shouldEqual "-1"
          }

          "has children elements" which {

            "include a h2 tag that" should {

              lazy val hTwo = div.select("h2").get(0)

              "have the id 'error-summary-heading'" in {
                hTwo.id() shouldEqual "error-summary-heading"
              }

              "have the class 'h3-heading'" in {
                hTwo.hasClass("h3-heading") shouldEqual true
              }

              "has the text" in {
                hTwo.text shouldEqual messages.errorSummaryHeading
              }
            }

            "include a ul element" which {

              lazy val ul = div.select("ul").get(0)

              "has the class 'js-error-summary-messages'" in {
                ul.hasClass("js-error-summary-messages") shouldEqual true
              }

              "has a child 'li' element that" should {

                lazy val li = ul.select("li").get(0)

                "have the role 'tooltip'" in {
                  li.attr("role") shouldEqual "tooltip"
                }

                "have the data-journey attribute of 'page:response:'" in {
                  li.attr("data-journey") shouldEqual "page:error:response"
                }

                "contain a child 'a' element" which {

                  lazy val a = li.select("a").get(0)

                  "should have the href '#response'" in {
                    a.attr("href") shouldEqual "#response"
                  }

                  "should have the id 'response-error-summary'" in {
                    a.id() shouldEqual "response-error-summary"
                  }

                  "should have the data-focus 'response'" in {
                    a.attr("data-focuses") shouldEqual "response"
                  }

                  s"should have the error message ${errorMessages.dummyError}" in {
                    a.text shouldEqual errorMessages.dummyError
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

          lazy val li = doc.select("li").get(0)

          "have the role 'tooltip'" in {
            li.attr("role") shouldEqual "tooltip"
          }

          "have the data-journey attribute of 'page:response:'" in {
            li.attr("data-journey") shouldEqual "page:error:dummyKey"
          }

          "contain a child 'a' element" which {

            lazy val a = li.select("a").get(0)

            "should have the href '#dummyKey'" in {
              a.attr("href") shouldEqual "#dummyKey"
            }

            "should have the id 'dummyKey-error-summary'" in {
              a.id() shouldEqual "dummyKey-error-summary"
            }

            "should have the data-focus 'dummyKey'" in {
              a.attr("data-focuses") shouldEqual "dummyKey"
            }

            s"should have the error message ${errorMessages.dummyError}" in {
              a.text shouldEqual errorMessages.dummyError
            }
          }
        }
      }
    }
  }
}
