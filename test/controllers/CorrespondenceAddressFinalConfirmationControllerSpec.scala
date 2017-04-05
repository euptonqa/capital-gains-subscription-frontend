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

import auth.{AuthorisedActions, CgtNROrganisation}
import common.Keys.KeystoreKeys
import connectors.KeystoreConnector
import data.MessageLookup
import models._
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubscriptionService
import traits.ControllerTestSpec
import auth.AuthenticatedNROrganisationAction
import common.Constants.ErrorMessages._
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class CorrespondenceAddressFinalConfirmationControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "dummy-unauthorised-url"
  val validBusinessData = ReviewDetails("", None, Address("", "", None, None, None, ""), "123456789", "123456789",
    isAGroup = false, directMatch = false, None)

  def createMockActions(valid: Boolean = false): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](1)
            val organisation = CgtNROrganisation(mock[AuthContext])
            Action.async(action(organisation))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  def createMockPostController(companyAddressModel: Option[CompanyAddressModel],
                               referenceResponse: Future[Option[SubscriptionReference]],
                               businessData: Option[ReviewDetails],
                               mockedActions: AuthorisedActions,
                               contactDetailsModel: Option[ContactDetailsModel]): CorrespondenceAddressFinalConfirmationController = {

    val mockService = mock[SubscriptionService]
    val mockKeystoreConnector = mock[KeystoreConnector]

    when(mockKeystoreConnector.fetchAndGetFormData[CompanyAddressModel](ArgumentMatchers.eq(KeystoreKeys.correspondenceAddressKey))(ArgumentMatchers.any(),
      ArgumentMatchers.any()))
      .thenReturn(Future.successful(companyAddressModel))

    when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsModel](ArgumentMatchers.eq(KeystoreKeys.contactDetailsKey))(ArgumentMatchers.any(),
      ArgumentMatchers.any()))
      .thenReturn(Future.successful(contactDetailsModel))

    when(mockKeystoreConnector.fetchAndGetBusinessData()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(businessData))

    when(mockService.getSubscriptionResponseCompany(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(referenceResponse)

    new CorrespondenceAddressFinalConfirmationController(mockConfig, messagesApi, mockedActions, mockService, mockKeystoreConnector)
  }


  "Calling .correspondenceAddressFinalConfirmation" when {

    "the business data, correspondence address and contact details are supplied" should {
      lazy val actions = createMockActions(valid = true)
      lazy val controller = createMockPostController(Some(CompanyAddressModel(Some(""), Some(""), Some(""), Some(""), Some(""), Some(""))),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData),
        actions,
        Some(ContactDetailsModel("", "", "")))
      lazy val result = controller.correspondenceAddressFinalConfirmation(FakeRequest("GET", ""))
      lazy val doc = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the final confirmation page" in {
        doc.title() shouldBe MessageLookup.ReviewBusinessDetails.title
      }
    }

    "no business data is found" should {
      lazy val actions = createMockActions(valid = true)
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        None, actions, Some(ContactDetailsModel("", "", "")))
      lazy val ex = intercept[Exception] {
        await(controller.correspondenceAddressFinalConfirmation(FakeRequest("GET", "")))
      }

      s"throw an exception with text $businessDataNotFound" in {
        ex.getMessage shouldEqual businessDataNotFound
      }
    }

    "no correspondence address is found" should {
      lazy val actions = createMockActions(valid = true)
      lazy val controller = createMockPostController(None,
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData), actions, Some(ContactDetailsModel("", "", "")))
      lazy val ex = intercept[Exception] {
        await(controller.correspondenceAddressFinalConfirmation(FakeRequest("GET", "")))
      }

      s"throw an exception with text $businessDataNotFound" in {
        ex.getMessage shouldEqual businessDataNotFound
      }
    }

    "no contact details are found" should {
      lazy val actions = createMockActions(valid = true)
      lazy val controller = createMockPostController(Some(CompanyAddressModel(Some(""), Some(""), Some(""), Some(""), Some(""), Some(""))),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData), actions, None)
      lazy val ex = intercept[Exception] {
        await(controller.correspondenceAddressFinalConfirmation(FakeRequest("GET", "")))
      }

      s"throw an exception with text $businessDataNotFound" in {
        ex.getMessage shouldEqual businessDataNotFound
      }
    }

    "the user is unauthorised" should {
      lazy val actions = createMockActions(valid = false)
      lazy val controller = createMockPostController(Some(CompanyAddressModel(Some(""), Some(""), Some(""), Some(""), Some(""), Some(""))),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData), actions, Some(ContactDetailsModel("", "", "")))
      lazy val result = controller.correspondenceAddressFinalConfirmation(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'dummy-unauthorised-url'" in {
        redirectLocation(result) shouldBe Some("dummy-unauthorised-url")
      }
    }
  }

  "Calling .submitCorrespondenceAddressFinalConfirmation" when {

    "the user is authorised correctly" should {

      "the cgt reference is retrieved correctly" should {
        lazy val actions = createMockActions(valid = true)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(Some(SubscriptionReference("CGT123456"))),
          Some(validBusinessData),
          actions,
          Some(ContactDetailsModel("", "", "")))
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 303" in {
          status(result) shouldBe 303
        }

        "load the cgt confirmation page" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CGTSubscriptionController.confirmationOfSubscription("CGT123456").url)
        }
      }

      "there is no keystore data available" should {
        lazy val actions = createMockActions(valid = true)
        lazy val controller = createMockPostController(None,
          Future.successful(Some(SubscriptionReference("CGT123456"))),
          None,
          actions,
          None)
        lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

        "have a status of 400" in {
          status(result) shouldBe 400
        }
      }

      "no business data is returned" should {
        lazy val actions = createMockActions(valid = true)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(Some(SubscriptionReference("CGT123456"))),
          None,
          actions,
          Some(ContactDetailsModel("", "", "")))
        lazy val ex = intercept[Exception] {
          await(controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", "")))
        }

        s"throw an exception with text $businessDataNotFound" in {
          ex.getMessage shouldEqual businessDataNotFound
        }
      }

      "no CGT reference is returned" should {
        lazy val actions = createMockActions(valid = true)
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.successful(None),
          Some(validBusinessData),
          actions,
          Some(ContactDetailsModel("", "", "")))
        lazy val ex = intercept[Exception] {
          await(controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", "")))
        }

        s"throw an exception with text $failedToEnrolCompany" in {
          ex.getMessage shouldEqual failedToEnrolCompany
        }
      }

      "an error occurs during subscription" should {
        lazy val actions = createMockActions(valid = true)
        lazy val exception = new Exception("testMessage")
        lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
          Future.failed(exception),
          Some(validBusinessData),
          actions,
          Some(ContactDetailsModel("", "", "")))
        lazy val ex = intercept[Exception] {
          await(controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", "")))
        }

        "throw an exception with text 'testMessage'" in {
          ex.getMessage shouldEqual "testMessage"
        }
      }
    }

    "the user is not authorised correctly" should {

      lazy val actions = createMockActions()
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData),
        actions,
        Some(ContactDetailsModel("", "", "")))
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
