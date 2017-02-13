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

import assets.ControllerTestSpec
import auth.{AuthorisedActions, CgtNROrganisation}
import builders.TestUserBuilder
import connectors.KeystoreConnector
import models.{CompanyAddressModel, ReviewDetails, SubscriptionReference}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubscriptionService
import types.AuthenticatedNROrganisationAction
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class CorrespondenceAddressFinalConfirmationControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "dummy-unauthorised-url"
  val validBusinessData = ReviewDetails("", None, mock[CompanyAddressModel], "123456789", "123456789",
    isAGroup = false, directMatch = false, None)

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithNINO): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](0)
            val organisation = CgtNROrganisation(authContext)
            Action.async(action(organisation))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  def createMockPostController(companyAddressModel: Option[CompanyAddressModel],
                               referenceResponse: Future[Option[SubscriptionReference]],
                               businessData: Option[ReviewDetails],
                               mockedActions: AuthorisedActions): CorrespondenceAddressFinalConfirmationController = {

    val mockService = mock[SubscriptionService]
    val mockKeystoreConnector = mock[KeystoreConnector]

    when(mockKeystoreConnector.fetchAndGetFormData[CompanyAddressModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(companyAddressModel))

    when(mockKeystoreConnector.fetchAndGetBusinessData()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(businessData))

    when(mockService.getSubscriptionResponseCompany(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(referenceResponse)

    new CorrespondenceAddressFinalConfirmationController(mockedActions, mockService, mockKeystoreConnector)
  }


  "Calling .correspondenceAddressFinalConfirmation" when {

  }

  "Calling .submitCorrespondenceAddressFinalConfirmation" when {

    "the user is authorised correctly" should {



      "the cgt reference is retrieved correctly" should {
        lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(Some(SubscriptionReference("CGT123456"))),
          Some(validBusinessData), actions)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 303" in {
          status(result) shouldBe 303
        }

        "load the cgt confirmation page" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CGTSubscriptionController.confirmationOfSubscription("CGT123456").url)
        }
      }

      "there is no keystore data available" should {
        lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
        lazy val controller = createMockPostController(None, Future.successful(Some(SubscriptionReference("CGT123456"))), None, actions)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 400" in {
          status(result) shouldBe 400
        }
      }

      "no business data is returned" should {
        lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(Some(SubscriptionReference("CGT123456"))), None, actions)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 500" in {
          status(result) shouldBe 500
        }
      }

      "no CGT reference is returned" should {
        lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(None), Some(validBusinessData), actions)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 500" in {
          status(result) shouldBe 500
        }
      }

      "an error occurs during subscription" should {
        lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
        lazy val exception = new Exception("testMessage")
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.failed(exception),
          Some(validBusinessData), actions)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 500" in {
          status(result) shouldBe 500
        }
      }
    }

    "the user is not authorised correctly" should {

      lazy val actions = createMockActions()
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData), actions)
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the correct error page" in {
        redirectLocation(result) shouldBe Some("dummy-unauthorised-url")
      }
    }
  }
}
