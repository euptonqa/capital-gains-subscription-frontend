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

import auth.{AuthorisedForCGT, CGTUser}
import config.ApplicationConfig
import connectors.FrontendAuthorisationConnector
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class ResidentIndividualSubscriptionControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  private type AsyncPlayRequest = Request[AnyContent] => Future[Result]
  private type AsyncUserRequest = CGTUser => AsyncPlayRequest

  def createMockAppConfig(): ApplicationConfig = {
    val mockAppConfig = mock[ApplicationConfig]

    when(mockAppConfig.governmentGateway)
      .thenReturn("gg-login")

    when(mockAppConfig.individualResident)
      .thenReturn("test")

    mockAppConfig
  }

  def createMockAuthority(valid: Boolean = false): AuthorisedForCGT = {

    val mockConnector = mock[FrontendAuthorisationConnector]

    val mockAuthorised = {
      if (valid) {
        new AuthorisedForCGT(createMockAppConfig(), mock[AuthorisationService], mockConnector) {

          override val authorised = new AuthorisedBy(mock[TaxRegime]) {
            override def async(action: AsyncUserRequest): Action[AnyContent] = {
              Action.async(action(mock[CGTUser]))
            }
          }
        }
      }
      else {
        new AuthorisedForCGT(createMockAppConfig(), mock[AuthorisationService], mockConnector) {
        }
      }
    }
    mockAuthorised
  }

  "Calling .residentIndividualSubscription" when {

    "provided with a valid user" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val service = createMockAuthority(true)

      lazy val target = new ResidentIndividualSubscriptionController(service, createMockAppConfig())
      lazy val result = target.residentIndividualSubscription(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the hello world page" in {
        redirectLocation(result) shouldBe Some(routes.HelloWorld.helloWorld().url)
      }
    }

    "provided with an invalid user" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val service = createMockAuthority()

      lazy val target = new ResidentIndividualSubscriptionController(service, createMockAppConfig())
      lazy val result = target.residentIndividualSubscription(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the mock gg-login page" in {
        redirectLocation(result) shouldBe Some("gg-login?" +
          "continue=%2Fcapital-gains-tax%2Fsubscription%2Fresident%2Findividual" +
          "&origin=capital-gains-subscription-frontend")
      }
    }
  }
}
