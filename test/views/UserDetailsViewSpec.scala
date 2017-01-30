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
import config.AppConfig
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import uk.gov.hmrc.play.test.UnitSpec
import views.html.userDetails

class UserDetailsViewSpec extends UnitSpec with OneAppPerSuite with FakeRequestHelper with I18nSupport {

  val injector: Injector = app.injector
  val appConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The User Details view" should {
    lazy val view = userDetails(appConfig)
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${UserDetails.title}'" in {
        doc.select("h1").text() shouldBe UserDetails.title
      }
    }
  }

}
