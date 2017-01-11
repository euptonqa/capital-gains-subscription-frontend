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

package controllers

import assets.{FakeRequestHelper, MessageLookup}
import config.AppConfig
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._

class IncorrectAffinityGroupControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper {

  val injector: Injector = fakeApplication.injector

  def appConfig: AppConfig = injector.instanceOf[AppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  val controller: IncorrectAffinityGroupController = new IncorrectAffinityGroupController(appConfig, messagesApi)

  "Calling .incorrectAffinityGroup" when {

    "provided with an valid userType" should {
      lazy val result = controller.incorrectAffinityGroup("company")(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the invalid affinity type page" in {
        Jsoup.parse(bodyOf(result)).title() shouldBe MessageLookup.InvalidAffinityGroup.title
      }
    }

    "provided with an invalid userType" should {
      lazy val result = controller.incorrectAffinityGroup("")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "should redirect to the select a user type page" in {
        redirectLocation(result) shouldBe Some("")
      }
    }
  }
}
