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

import config.AppConfig
import connectors.KeystoreConnector
import data.MessageLookup.{CGTSubscriptionConfirm => messages}
import models.CallbackUrlModel
import org.jsoup._
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class CGTSubscriptionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val fakeRequest = FakeRequest("GET", "/")
  val injector: Injector = fakeApplication.injector

  def appConfig: AppConfig = injector.instanceOf[AppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  val mockKeystoreConnector: KeystoreConnector = mock[KeystoreConnector]

  def setupPostController(keystoreResponse: Option[CallbackUrlModel]): CGTSubscriptionController = {

    when(mockKeystoreConnector.fetchAndGetFormData[CallbackUrlModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(keystoreResponse))

    new CGTSubscriptionController(mockKeystoreConnector, appConfig, messagesApi)
  }

  val target = new CGTSubscriptionController(mockKeystoreConnector, appConfig, messagesApi)

  implicit val mat: akka.stream.Materializer = mock[akka.stream.Materializer]

  "GET /resident/confirmation" should {

    lazy val result = target.confirmationOfSubscription("testString")(fakeRequest)
    lazy val view = Jsoup.parse(bodyOf(result))

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "display the confirmationOfSubscription screen" in {
      view.title() shouldEqual messages.title
    }
  }

  "POST /resident/confirmation" when {

    "keystore manages to retrieve a callback url" should {
      lazy val mockController = setupPostController(Some(CallbackUrlModel("returned-url")))
      lazy val result = mockController.submitConfirmationOfSubscription(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("returned-url")
      }
    }

    "keystore fails to retrieve a callbackUrl" should {
      lazy val mockController = setupPostController(None)
      lazy val ex = intercept[Exception] {
        await(mockController.submitConfirmationOfSubscription(fakeRequest))
      }

      s"return an Exception with text Failed to find a callback URL" in {
        ex.getMessage shouldEqual "Failed to find a callback URL"
      }
    }
  }
}
