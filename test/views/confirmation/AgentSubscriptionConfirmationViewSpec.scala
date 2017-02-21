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

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, ResolverStyle}

import assets.ViewTestSpec
import org.jsoup.Jsoup
import views.html.confirmation.agentSubscriptionConfirmation
import assets.MessageLookup.{AgentConfirmation => messages}

class AgentSubscriptionConfirmationViewSpec extends ViewTestSpec {

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT)

  "The agentSubscriptionConfirmation view" should {

    val subscriptionDate: LocalDate = LocalDate.parse("20/2/2017", dateFormatter)

    lazy val view = agentSubscriptionConfirmation(appConfig, "ARN123456", subscriptionDate, "Agency Name")
    lazy val doc = Jsoup.parse(view.body)

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    "have a heading" which {

      s"has the text '${messages.title}'" in {
        doc.select("h1").text() shouldEqual messages.title
      }

      "should have the class visually hidden" in {
        doc.select("h1").hasClass("visuallyhidden") shouldEqual true
      }
    }

    "have content" which {

      "has the first div" which {

        lazy val greenBanner = doc.select("div #confirmation-banner")

        "has the class 'transaction-banner--complete'" in {
          greenBanner.hasClass("transaction-banner--complete") shouldEqual true
        }

        "has the first paragraph" which {

          lazy val p1 = greenBanner.select("p").get(0)

          "contains a strong tag" which {

            "has the class 'heading-large'" in {
              p1.select("strong").hasClass("heading-large") shouldEqual true
            }

            s"has the text '${messages.setUp} Agency Name ${messages.forCgt}'" in {
              p1.select("strong").text shouldEqual s"${messages.setUp} Agency Name ${messages.forCgt}"
            }
          }
        }

        "has the second paragraph" which {

          lazy val p2 = greenBanner.select("p").get(1)

          "has the class 'confirm-padding-below'" in {
            p2.hasClass("confirm-padding-below") shouldEqual true
          }

          "contains a strong tag" which {

            "has the class 'heading-medium'" in {
              p2.select("strong").hasClass("heading-medium") shouldEqual true
            }

            "has the text 'on 20 February 2017'" in {
              p2.select("strong").text shouldEqual "on 20 February 2017"
            }
          }
        }

        "has the third paragraph" which {

          lazy val p3 = greenBanner.select("p").get(2)

          "contains a strong tag" which {

            "has the class 'heading-medium'" in {
              p3.select("strong").hasClass("heading-medium") shouldEqual true
            }

            s"has the text '${messages.yourArn} ARN123456'" in {
              p3.select("strong").text shouldEqual s"${messages.yourArn} ARN123456"
            }
          }
        }
      }

      "has the second div" which {

        lazy val content = doc.select("div #instruction-information")

        "has the class form-group" in {
          content.hasClass("form-group") shouldEqual true
        }

        "has a first paragraph that" should {

          lazy val p1 = content.select("p").get(0)

          "contains a strong tag" which {

            "has the class 'heading-medium'" in {
              p1.select("strong").hasClass("heading-medium") shouldEqual true
            }

            s"has the text '${messages.whatHappensNext}'" in {
              p1.select("strong").text shouldEqual s"${messages.whatHappensNext}"
            }
          }
        }

        "has a second paragraph that" should {

          lazy val p2 = content.select("p").get(1)

          s"has the text '${messages.addClients}'" in {
            p2.text shouldEqual s"${messages.addClients}"
          }
        }
      }

      "has a button that" should {

        lazy val button = doc.select("#continue")

        "has the class button" in {
          button.hasClass("button") shouldEqual true
        }

        "has a link to agent service" in {
          button.attr("href") shouldEqual "http://localhost:9773/clients/manage"
        }
      }
    }
  }
}
