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

import auth._
import common.Keys.KeystoreKeys._
import config.WSHttp
import connectors.{KeystoreConnector, SubscriptionConnector}
import data.MessageLookup.{CGTSubscriptionConfirm => messages}
import data.TestUserBuilder
import models.{CallbackUrlModel, Enrolment, SubscriptionReference}
import org.jsoup._
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.http.Status
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import services.SubscriptionService
import traits.ControllerTestSpec
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class CGTSubscriptionControllerSpec extends ControllerTestSpec {

  val testOnlyUnauthorisedLoginUri = "just-a-test"

  def createNROrgMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.strongUserAuthContext): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](1)
            val organisation = CgtNROrganisation(authContext)
            Action.async(action(organisation))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  def createIndvMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithNINO): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]
    if (valid) {
      when(mockActions.authorisedNonResidentIndividualAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedIndividualAction](1)
            val individual = CgtIndividual(authContext)
            Action.async(action(individual))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  def setupController(mockActions: AuthorisedActions,
                     callbackUrl: Option[CallbackUrlModel] = Some(CallbackUrlModel("context/test"))): CGTSubscriptionController = {

    val sessionService = mock[KeystoreConnector]

    when(sessionService.fetchAndGetFormData[CallbackUrlModel](ArgumentMatchers.eq(callbackUrlKey))(any(), any()))
      .thenReturn(Future.successful(callbackUrl))

    new CGTSubscriptionController(sessionService, mockActions, mockConfig, messagesApi)
  }

//  "GET /individual/confirmation" should {
//
//    "an authorised user made the request" should {
//      lazy val actions = createIndvMockActions()
//      lazy val controller = setupController(actions)
//      val fakeRequest = FakeRequest("GET", "/")
//      lazy val result = controller.confirmationOfSubscriptionResidentIndv("testString")(fakeRequest)
//      lazy val view = Jsoup.parse(bodyOf(result))
//
//      "return 200" in {
//        status(result) shouldBe Status.OK
//      }
//
//      "display the confirmationOfSubscription screen" in {
//        view.title() shouldEqual messages.title
//      }
//    }
//  }

//  "GET /non-resident/confirmation" should {
//
//    lazy val result = target.confirmationOfSubscriptionNonResIndv("testString")(fakeRequest)
//    lazy val view = Jsoup.parse(bodyOf(result))
//
//    "return 200" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "display the confirmationOfSubscription screen" in {
//      view.title() shouldEqual messages.title
//    }
//  }
//
//  "GET /organisation/confirmation" should {
//
//    lazy val result = target.confirmationOfSubscriptionOrganisation("testString")(fakeRequest)
//    lazy val view = Jsoup.parse(bodyOf(result))
//
//    "return 200" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "display the confirmationOfSubscription screen" in {
//      view.title() shouldEqual messages.title
//    }
//  }
//
//  "POST /individual/confirmation" when {
//
//    "keystore manages to retrieve a callback url" should {
//      lazy val mockController = setupPostController(Some(CallbackUrlModel("returned-url")))
//      lazy val result = mockController.submitConfirmationOfSubscriptionResidentIndv(fakeRequest)
//
//      "return 303" in {
//        status(result) shouldBe Status.SEE_OTHER
//      }
//
//      "redirect to the iForm page" in {
//        redirectLocation(result).get should include("returned-url")
//      }
//    }
//
//    "keystore fails to retrieve a callbackUrl" should {
//      lazy val mockController = setupPostController(None)
//      lazy val ex = intercept[Exception] {
//        await(mockController.submitConfirmationOfSubscriptionResidentIndv(fakeRequest))
//      }
//
//      s"return an Exception with text Failed to find a callback URL" in {
//        ex.getMessage shouldEqual "Failed to find a callback URL"
//      }
//    }
//  }
//
//  "POST /non-resident/confirmation" when {
//
//    "keystore manages to retrieve a callback url" should {
//      lazy val mockController = setupPostController(Some(CallbackUrlModel("returned-url")))
//      lazy val result = mockController.submitConfirmationOfSubscriptionNonResIndv(fakeRequest)
//
//      "return 303" in {
//        status(result) shouldBe Status.SEE_OTHER
//      }
//
//      "redirect to the iForm page" in {
//        redirectLocation(result).get should include("returned-url")
//      }
//    }
//
//    "keystore fails to retrieve a callbackUrl" should {
//      lazy val mockController = setupPostController(None)
//      lazy val ex = intercept[Exception] {
//        await(mockController.submitConfirmationOfSubscriptionNonResIndv(fakeRequest))
//      }
//
//      s"return an Exception with text Failed to find a callback URL" in {
//        ex.getMessage shouldEqual "Failed to find a callback URL"
//      }
//    }
//  }
//
//  "POST /organisation/confirmation" when {
//
//    "keystore manages to retrieve a callback url" should {
//      lazy val mockController = setupPostController(Some(CallbackUrlModel("returned-url")))
//      lazy val result = mockController.submitConfirmationOfSubscriptionOrganisation(fakeRequest)
//
//      "return 303" in {
//        status(result) shouldBe Status.SEE_OTHER
//      }
//
//      "redirect to the iForm page" in {
//        redirectLocation(result).get should include("returned-url")
//      }
//    }
//
//    "keystore fails to retrieve a callbackUrl" should {
//      lazy val mockController = setupPostController(None)
//      lazy val ex = intercept[Exception] {
//        await(mockController.submitConfirmationOfSubscriptionOrganisation(fakeRequest))
//      }
//
//      s"return an Exception with text Failed to find a callback URL" in {
//        ex.getMessage shouldEqual "Failed to find a callback URL"
//      }
//    }
//  }
}
