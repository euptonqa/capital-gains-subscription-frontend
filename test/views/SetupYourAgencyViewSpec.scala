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

import data.MessageLookup
import data.MessageLookup.SetupYourAgency._
import org.jsoup.Jsoup
import traits.ViewTestSpec
import views.html.setupYourAgency

class SetupYourAgencyViewSpec extends ViewTestSpec {

  "The setup your agency view" should {
    lazy val view = setupYourAgency(appConfig)
    lazy val doc = Jsoup.parse(view.body)

    s"have the title of '$title'" in {
      doc.title() shouldBe title
    }

    "contain a header" which {
      lazy val header = doc.select("h1")

      "has the class 'heading-xlarge'" in {
        header.attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '$title'" in {
        header.text() shouldBe title
      }
    }

    "contain a leading paragraph" which {
      lazy val paragraph = doc.select("main p").get(1)

      "has the class 'lede'" in {
        paragraph.attr("class") shouldBe "lede"
      }

      s"has the text '$leadParagraph'" in {
        paragraph.text() shouldBe leadParagraph
      }
    }

    "contain a secondary header" which {
      lazy val header = doc.select("h2")

      "has the class 'heading-xlarge'" in {
        header.attr("class") shouldBe "heading-small"
      }

      s"has the message '$listTitle'" in {
        header.text() shouldBe listTitle
      }
    }

    "contain a list" which {
      lazy val list = doc.select("ol")

      "has the class 'list-number form-group'" in {
        list.attr("class") shouldBe "list-number form-group"
      }

      s"has a first entry with the text $listOne" in {
        list.select("li").get(0).text() shouldBe listOne
      }

      s"has a second entry with the text $listTwo" in {
        list.select("li").get(1).text() shouldBe listTwo
      }
    }

    "contain a button" which {
      lazy val button = doc.select("main a").get(1)

      "has the class 'button'" in {
        button.attr("class") shouldBe "button"
      }

      s"has the text '${MessageLookup.Common.continue}'" in {
        button.text() shouldBe MessageLookup.Common.continue
      }

      "has an href to business customer frontend" in {
        button.attr("href") should include("business-customer/business-verification/capital-gains-tax-agents")
      }

      "has an id of continue-button" in {
        button.id() shouldBe "continue-button"
      }
    }
  }
}
