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
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import assets.MessageLookup.{InvalidAffinityGroup => messages}
import org.jsoup._
import views.html.errors.errorInvalidUser
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

class IncorrectAffinityGroupViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper {

  "The something or other view for an agent" should {

    lazy val view = errorInvalidUser()
    lazy val doc = Jsoup.parse(view.body)

    s"Display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    s"Have the heading ${messages.title}" in {
      doc.select("h1").text shouldEqual messages.title
    }

    "Have the content" which {

      s"Should have a first paragraph with the text ${messages.textOne}" in {
        doc.select("p").get(1).text shouldEqual messages.textOne
      }

      s"Should have a second paragraph" which {

        s"should have the text ${messages.textTwo}" in {
          doc.select("p").get(2).text shouldEqual messages.textTwo
        }

        s"should have a link" which {

          s"has the text ${messages.signOut}" in {
            doc.select("p").get(2).select("a").text shouldEqual messages.signOut
          }

          s"has a href to https://www.tax.service.gov.uk/gg/sign-in?continue=/account" in {
            doc.select("p").get(2).select("a").attr("href") shouldEqual "https://www.tax.service.gov.uk/gg/sign-in?continue=/account"
          }
        }
      }

      s"Should have a third paragraph" which {

        "should have the text ${messages.textThreeAgent}" in {
          doc.select("p").get(3).text shouldEqual messages.textThreeAgent
        }

        s"should have a link" which {

          s"has the text ${messages.linkTextAgent}" in {
            doc.select("p").get(3).select("a").text shouldEqual messages.linkTextAgent
          }

          s"has a href to https://www.tax.service.gov.uk/gg/sign-in?continue=/account" in {
            doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.tax.service.gov.uk/gg/sign-in?continue=/account"
          }
        }

      }
    }

    "Have the link for agent - https://www.gov.uk/guidance/self-assessment-for-agents-online-service" in {

    }
  }

  "The something or other view for a company" should {

    s"Display a title of ${messages.title}" in {

    }

    "Have the content" which {

      s"Should have a first paragraph with the text ${messages.textOne}" in {

      }

      s"Should have a second paragraph" which {

        s"should have the text ${messages.textOne}" in {

        }

        s"should have a link" which {

          s"has the text ${messages.signOut}" in {

          }

          s"has a href to The Government Gateway Login" in {

          }

        }

      }

      s"Should have a third paragraph with the text ${messages.textThreeCompany}" in {

      }

    }

    "Have the link for company - https://www.gov.uk/tax-when-your-company-sells-assets" in {

    }

  }

}
