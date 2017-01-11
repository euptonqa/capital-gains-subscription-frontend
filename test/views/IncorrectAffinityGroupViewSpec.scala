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
import config.AppConfig
import org.jsoup._
import org.scalatest.mock.MockitoSugar
import views.html.errors.errorInvalidUser
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.inject.Injector

class IncorrectAffinityGroupViewSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar {

  val injector: Injector = fakeApplication.injector

  val appConfig: AppConfig = injector.instanceOf[AppConfig]

  implicit val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The invalid affinity group view for an agent" should {

    lazy val view = errorInvalidUser("agent", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    s"have the heading ${messages.title}" in {
      doc.select("h1").text shouldEqual messages.title
    }

    "have the content" which {

      s"Should have a first paragraph with the text ${messages.textOne}" in {
        doc.select("p").get(1).text shouldEqual messages.textOne
      }

      "should have a second paragraph" which {

        s"should have the text ${messages.textTwo}" in {
          doc.select("p").get(2).text shouldEqual messages.textTwo
        }

        "should have a link" which {

          s"has the text ${messages.signOut}" in {
            doc.select("p").get(2).select("a").text shouldEqual messages.signOut
          }

          "has a href to https://www.tax.service.gov.uk/gg/sign-in?continue=/account" in {
            doc.select("p").get(2).select("a").attr("href") shouldEqual "https://www.tax.service.gov.uk/gg/sign-in?continue=/account"
          }
        }
      }

      "should have a third paragraph" which {

        s"should have the text ${messages.textThreeAgent}" in {
          doc.select("p").get(3).text shouldEqual messages.textThreeAgent
        }

        "should have a link" which {

          s"has the text ${messages.linkTextAgent}" in {
            doc.select("p").get(3).select("a").text shouldEqual messages.linkTextAgent
          }

          "has a href to https://www.gov.uk/guidance/self-assessment-for-agents-online-service" in {
            doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/guidance/self-assessment-for-agents-online-service"
          }
        }

      }
    }
  }

  "The invalid affinity group view for a company" should {

    lazy val view = errorInvalidUser("company", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "should have a third paragraph" which {

      s"should have the text ${messages.textThreeCompany}" in {
        doc.select("p").get(3).text shouldEqual messages.textThreeCompany
      }

      "should have a link" which {

        s"has the text ${messages.linkTextCompany}" in {
          doc.select("p").get(3).select("a").text shouldEqual messages.linkTextCompany
        }

        "has a href to https://www.gov.uk/tax-when-your-company-sells-assets" in {
          doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/tax-when-your-company-sells-assets"
        }
      }
    }
  }

  "The invalid affinity group view for a charity" should {

    lazy val view = errorInvalidUser("charity", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "should have a third paragraph" which {

      s"should have the text ${messages.textThreeCharity}" in {
        doc.select("p").get(3).text shouldEqual messages.textThreeCharity
      }

      "should have a link" which {

        s"has the text ${messages.linkTextCharity}" in {
          doc.select("p").get(3).select("a").text shouldEqual messages.linkTextCharity
        }

        "has a href to https://www.gov.uk/charities-and-tax/tax-reliefs" in {
          doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/charities-and-tax/tax-reliefs"
        }
      }
    }
  }

  "The invalid affinity group view for a partnership" should {

    lazy val view = errorInvalidUser("partnership", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "should have a third paragraph" which {

      s"should have the text ${messages.textThreePartnership}" in {
        doc.select("p").get(3).text shouldEqual messages.textThreePartnership
      }

      "should have a link" which {

        s"has the text ${messages.linkTextPartnership}" in {
          doc.select("p").get(3).select("a").text shouldEqual messages.linkTextPartnership
        }

        "has a href to https://www.gov.uk/government/publications/partnerships-and-capital-gains-tax-hs288-self-assessment-helpsheet/" +
          "hs288-partnerships-and-capital-gains-tax-2016" in {
          doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/government/publications/" +
            "partnerships-and-capital-gains-tax-hs288-self-assessment-helpsheet/hs288-partnerships-and-capital-gains-tax-2016"
        }
      }
    }
  }

  "The invalid affinity group view for a trust" should {

    lazy val view = errorInvalidUser("trust", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "should have a third paragraph" which {

      s"should have the text ${messages.textThreeTrust}" in {
        doc.select("p").get(3).text shouldEqual messages.textThreeTrust
      }

      "should have a link" which {

        s"has the text ${messages.linkTextTrust}" in {
          doc.select("p").get(3).select("a").text shouldEqual messages.linkTextTrust
        }

        "has a href to https://www.gov.uk/guidance/trusts-and-capital-gains-tax" in {
          doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/guidance/trusts-and-capital-gains-tax"
        }
      }
    }
  }

  "The invalid affinity group view for a pension trust" should {

    lazy val view = errorInvalidUser("pensionTrust", appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "should have a third paragraph" which {

      s"should have the text ${messages.textThreePensionTrust}" in {
        doc.select("p").get(3).text shouldEqual messages.textThreePensionTrust
      }

      "should have a link" which {

        s"has the text ${messages.linkTextPensionTrust}" in {
          doc.select("p").get(3).select("a").text shouldEqual messages.linkTextPensionTrust
        }

        "has a href to https://www.gov.uk/guidance/pension-trustees-investments-and-tax" in {
          doc.select("p").get(3).select("a").attr("href") shouldEqual "https://www.gov.uk/guidance/pension-trustees-investments-and-tax"
        }
      }
    }
  }
}
