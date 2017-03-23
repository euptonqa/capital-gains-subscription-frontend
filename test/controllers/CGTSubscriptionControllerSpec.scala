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

import auth.GlobalRedirects
import config.AppConfig
import connectors.FrontendAuthCoreConnector
import data.MessageLookup
import data.MessageLookup.{CGTSubscriptionConfirm => messages}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{InsufficientConfidenceLevel, InsufficientEnrolments, MissingBearerToken}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._

import scala.concurrent.Future

class CGTSubscriptionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val fakeRequest = FakeRequest("GET", "/")
  val injector: Injector = fakeApplication.injector

  lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]

  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  lazy val redirects: GlobalRedirects = injector.instanceOf[GlobalRedirects]

  implicit val mat: akka.stream.Materializer = mock[akka.stream.Materializer]

  def setupController(authResponse: Future[Unit]): CGTSubscriptionController = {
    val mockConnector = mock[FrontendAuthCoreConnector]

    when(mockConnector.authorise[Any](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(authResponse)

    new CGTSubscriptionController(appConfig, messagesApi, mockConnector, redirects)
  }

  "Calling the controller method" when {

    "the user is not logged in" should {
      lazy val controller = setupController(Future.failed(new MissingBearerToken))
      lazy val result = controller.testAuth("CGTREF")(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the gg-login page" in {
        redirectLocation(result).get shouldBe "http://localhost:9025/gg/sign-in" +
          "?continue=http%3A%2F%2Flocalhost%3A9771%2Fcapital-gains-tax%2Fsubscription%2Fresident%2Findividual" +
          "&origin=capital-gains-subscription-frontend"
      }
    }

    "the user does not have the correct enrolments" should {
      lazy val controller = setupController(Future.failed(new InsufficientEnrolments))
      lazy val result = controller.testAuth("CGTREF")(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the unauthorised page" in {
        redirectLocation(result).get shouldBe "http://localhost:9771/capital-gains-tax/subscription/not-authorised"
      }
    }

    "the user does not have the correct confidence level" should {
      lazy val controller = setupController(Future.failed(new InsufficientConfidenceLevel))
      lazy val result = controller.testAuth("CGTREF")(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the iv-uplift page" in {
        redirectLocation(result).get shouldBe "http://localhost:9948/mdtp/uplift" +
          "?origin=capital-gains-subscription-frontend" +
          "&confidenceLevel=200" +
          "&completionURL=http%3A%2F%2Flocalhost%3A9771%2Fcapital-gains-tax%2Fsubscription%2Fresident%2Findividual" +
          "&failureURL=http%3A%2F%2Flocalhost%3A9771%2Fcapital-gains-tax%2Fsubscription%2Fnot-authorised"
      }
    }

    "the user is authorised" should {
      lazy val controller = setupController(Future.successful())
      lazy val result = controller.testAuth("CGTREF")(FakeRequest("GET", ""))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the confirmation page" in {
        lazy val doc = Jsoup.parse(bodyOf(result))

        doc.title() shouldBe MessageLookup.CGTSubscriptionConfirm.title
      }
    }
  }
}
