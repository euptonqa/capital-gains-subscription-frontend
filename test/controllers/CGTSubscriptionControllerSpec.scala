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
import connectors.KeystoreConnector
import data.MessageLookup.{CGTSubscriptionConfirm => messages}
import data.TestUserBuilder
import models.RedirectModel
import org.jsoup._
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.http.Status
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
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

  def createNonResidentIndvMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithoutNINO):
    AuthorisedActions = {

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
      when(mockActions.authorisedNonResidentIndividualAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  def createResidentIndvMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithNINO): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]
    if (valid) {
      when(mockActions.authorisedResidentIndividualAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedIndividualAction](1)
            val individual = CgtIndividual(authContext)
            Action.async(action(individual))
          }
        })
    }
    else {
      when(mockActions.authorisedResidentIndividualAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  def setupController(mockActions: AuthorisedActions,
                     callbackUrl: Option[RedirectModel] = Some(RedirectModel("context/test"))): CGTSubscriptionController = {

    val sessionService = mock[KeystoreConnector]

    when(sessionService.fetchAndGetFormData[RedirectModel](ArgumentMatchers.eq(redirect))(any(), any()))
      .thenReturn(Future.successful(callbackUrl))

    new CGTSubscriptionController(sessionService, mockActions, mockConfig, messagesApi)
  }

  "GET /individual/confirmation" should {

    "an authorised user made the request" should {
      lazy val actions = createResidentIndvMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "")
      lazy val result = controller.confirmationOfSubscriptionResidentIndv("testString")(fakeRequest)
      lazy val view = Jsoup.parse(bodyOf(result))

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "display the confirmationOfSubscription screen" in {
        view.title() shouldEqual messages.title
      }
    }

    "an unauthorised user made the request" should {
      lazy val actions = createResidentIndvMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "/")
      lazy val result = controller.confirmationOfSubscriptionResidentIndv("testString")(fakeRequest)

      "return 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'just-a-test'" in {
        redirectLocation(result) shouldBe Some("just-a-test")
      }
    }
  }

  "GET /non-resident/confirmation" should {

    "an authorised user made the request" should {
      lazy val actions = createNonResidentIndvMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "")
      lazy val result = controller.confirmationOfSubscriptionNonResIndv("testString")(fakeRequest)
      lazy val view = Jsoup.parse(bodyOf(result))

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "display the confirmationOfSubscription screen" in {
        view.title() shouldEqual messages.title
      }
    }

    "an unauthorised user made the request" should {
      lazy val actions = createNonResidentIndvMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "/")
      lazy val result = controller.confirmationOfSubscriptionNonResIndv("testString")(fakeRequest)

      "return 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'just-a-test'" in {
        redirectLocation(result) shouldBe Some("just-a-test")
      }
    }
  }

  "GET /organisation/confirmation" should {

    "an authorised user made the request" should {
      lazy val actions = createNROrgMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "")
      lazy val result = controller.confirmationOfSubscriptionCompany("testString")(fakeRequest)
      lazy val view = Jsoup.parse(bodyOf(result))

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "display the confirmationOfSubscription screen" in {
        view.title() shouldEqual messages.title
      }
    }

    "an unauthorised user made the request" should {
      lazy val actions = createNROrgMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("GET", "/")
      lazy val result = controller.confirmationOfSubscriptionCompany("testString")(fakeRequest)

      "return 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'just-a-test'" in {
        redirectLocation(result) shouldBe Some("just-a-test")
      }
    }
  }

  "POST /individual/confirmation" when {

    "keystore manages to retrieve a callback url and the user is authorised" should {
      lazy val actions = createResidentIndvMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionResidentIndv(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("context/test")
      }
    }

    "keystore manages to retrieve a callback url and the user is not authorised" should {
      lazy val actions = createResidentIndvMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionResidentIndv(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("just-a-test")
      }
    }

    "keystore fails to retrieve a callbackUrl" should {
      lazy val actions = createResidentIndvMockActions(valid = true)
      lazy val controller = setupController(mockActions = actions, callbackUrl = None)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionResidentIndv(fakeRequest)

      lazy val ex = intercept[Exception] {
        await(result)
      }

      s"return an Exception with text Failed to find a callback URL" in {
        ex.getMessage shouldEqual "Failed to find a callback URL"
      }
    }
  }

  "POST /non-resident/confirmation" when {

    "keystore manages to retrieve a callback url and the user is authorised" should {
      lazy val actions = createNonResidentIndvMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionNonResIndv(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("context/test")
      }
    }

    "keystore manages to retrieve a callback url and the user is not authorised" should {
      lazy val actions = createNonResidentIndvMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionNonResIndv(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("just-a-test")
      }
    }

    "keystore fails to retrieve a callbackUrl" should {
      lazy val actions = createNonResidentIndvMockActions(valid = true)
      lazy val controller = setupController(mockActions = actions, callbackUrl = None)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionNonResIndv(fakeRequest)

      lazy val ex = intercept[Exception] {
        await(result)
      }

      s"return an Exception with text Failed to find a callback URL" in {
        ex.getMessage shouldEqual "Failed to find a callback URL"
      }
    }
  }

  "POST /organisation/confirmation" when {

    "keystore manages to retrieve a callback url and the user is authorised" should {
      lazy val actions = createNROrgMockActions(valid = true)
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionCompany(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("context/test")
      }
    }

    "keystore manages to retrieve a callback url and the user is not authorised" should {
      lazy val actions = createNROrgMockActions()
      lazy val controller = setupController(actions)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionCompany(fakeRequest)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the iForm page" in {
        redirectLocation(result).get should include("just-a-test")
      }
    }

    "keystore fails to retrieve a callbackUrl" should {
      lazy val actions = createNROrgMockActions(valid = true)
      lazy val controller = setupController(mockActions = actions, callbackUrl = None)
      val fakeRequest = FakeRequest("POST", "/")
      lazy val result = controller.submitConfirmationOfSubscriptionCompany(fakeRequest)

      lazy val ex = intercept[Exception] {
        await(result)
      }

      s"return an Exception with text Failed to find a callback URL" in {
        ex.getMessage shouldEqual "Failed to find a callback URL"
      }
    }
  }
}
