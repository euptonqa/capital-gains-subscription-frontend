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

import data.MessageLookup.{UseRegisteredAddress => messages}
import auth.{AuthorisedActions, CgtNROrganisation}
import common.Keys.KeystoreKeys
import connectors.KeystoreConnector
import forms.YesNoForm
import models.{Address, ReviewDetails, YesNoModel}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import traits.ControllerTestSpec
import auth.AuthenticatedNROrganisationAction
import common.Constants.ErrorMessages._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class CorrespondenceAddressConfirmControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "dummy-unauthorised-url"

  def createMockActions(valid: Boolean = false): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](0)
            val organisation = CgtNROrganisation(mock[AuthContext])
            Action.async(action(organisation))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(any()))
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  object TestData {
    val businessAddress = Address(line_1 = "",
      line_2 = "",
      line_3 = None,
      line_4 = None,
      country = "")

    val businessDetails = ReviewDetails(businessName = "",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "",
      businessType = None,
      agentReferenceNumber = None)
  }

  "Calling .correspondenceAddressConfirm" when {

    "user is not authorised" should {

      val request = FakeRequest("", "")
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions()
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)
      lazy val result = target.correspondenceAddressConfirm(request)

      "return a return a 303 response code" in {
        status(result) shouldBe 303
      }

      "redirect to a login page" in {
        redirectLocation(result) shouldBe Some(unauthorisedLoginUri)
      }
    }

    "no details for the business exist in keystore" should {

      "return a return a 500 response code" should {

        val request = FakeRequest("", "")
        val stateService = mock[KeystoreConnector]
        val actions = createMockActions(true)
        val form = new YesNoForm(messagesApi)
        val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)

        when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(None))
        when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(None))

        lazy val ex = intercept[Exception] {
          await(target.correspondenceAddressConfirm(request))
        }

        s"throw an exception with text $businessDataNotFound" in {
          ex.getMessage shouldEqual businessDataNotFound
        }
      }
    }

    "a previous answer has NOT been supplied" should {

      val request = FakeRequest("", "")
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions(true)
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)
      lazy val document = Jsoup.parse(bodyOf(result))

      when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.businessDetails)))
      when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(None))

      lazy val result = target.correspondenceAddressConfirm(request)

      "return a return a 200 response code" in {
        status(result) shouldBe 200
      }

      "return the correct view" in {
        document.title shouldBe messages.title
      }
    }

    "a previous answer has been supplied" should {

      val request = FakeRequest("", "")
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions(true)
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)
      lazy val document = Jsoup.parse(bodyOf(result))

      when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.businessDetails)))
      when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(Some(YesNoModel(true))))

      lazy val result = target.correspondenceAddressConfirm(request)

      "return a return a 200 response code" in {
        status(result) shouldBe 200
      }

      "return the correct view" in {
        document.title shouldBe messages.title
      }
    }

  }

  "Calling .submitCorrespondenceAddressConfirm" when {

    "user is not authorised" should {

      val request = FakeRequest("", "")
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions()
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)
      lazy val result = target.submitCorrespondenceAddressConfirm(request)

      "return a return a 303 response code" in {
        status(result) shouldBe 303
      }

      "redirect to a login page" in {
        redirectLocation(result) shouldBe Some(unauthorisedLoginUri)
      }
    }

    "no details for the business exist in keystore" should {

      "return a return a 500 response code" should {

        val request = FakeRequest("", "")
        val stateService = mock[KeystoreConnector]
        val actions = createMockActions(true)
        val form = new YesNoForm(messagesApi)
        val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)

        when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(None))
        when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(None))

        lazy val ex = intercept[Exception] {
          await(target.submitCorrespondenceAddressConfirm(request))
        }

        s"throw an exception with text $businessDataNotFound" in {
          ex.getMessage shouldEqual businessDataNotFound
        }
      }
    }

    "an answer has NOT been supplied" should {

      val request = FakeRequest("", "")
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions(true)
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)
      lazy val document = Jsoup.parse(bodyOf(result))

      when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.businessDetails)))
      when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(None))

      lazy val result = target.submitCorrespondenceAddressConfirm(request)

      "return a return a 400 response code" in {
        status(result) shouldBe 400
      }

      "return the correct view" in {
        document.title shouldBe messages.title
      }
    }

    "user answers with 'Yes'" should {

      val request = FakeRequest("", "").withFormUrlEncodedBody(("response", "Yes"))
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions(true)
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)

      when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.businessDetails)))
      when(stateService.saveFormData(anyString(), any())(any(), any())).thenReturn(Future.successful(CacheMap("", Map.empty)))

      lazy val result = target.submitCorrespondenceAddressConfirm(request)

      "return a return a 303 response code" in {
        status(result) shouldBe 303
      }

      "redirect the user to the correct location" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ContactDetailsController.contactDetails().url)
      }

      "save the user response" in {
        verify(stateService, times(1)).saveFormData(ArgumentMatchers.eq(KeystoreKeys.useRegistrationAddressKey), any())(any(), any())
      }

      "save the business registration address" in {
        verify(stateService, times(1)).saveFormData(ArgumentMatchers.eq(KeystoreKeys.correspondenceAddressKey), any())(any(), any())
      }
    }


    "user answers with 'No'" should {

      val request = FakeRequest("", "").withFormUrlEncodedBody(("response", "No"))
      val stateService = mock[KeystoreConnector]
      val actions = createMockActions(true)
      val form = new YesNoForm(messagesApi)
      lazy val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)

      when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.businessDetails)))
      when(stateService.saveFormData(anyString(), any())(any(), any())).thenReturn(Future.successful(CacheMap("", Map.empty)))

      lazy val result = target.submitCorrespondenceAddressConfirm(request)

      "return a 303 response code" in {
        status(result) shouldBe 303
      }

      "redirect the user to the correct location" in {
        redirectLocation(result) shouldBe Some(controllers.routes.EnterCorrespondenceAddressController.enterCorrespondenceAddress().url)
      }

      "save the user response" in {
        verify(stateService, times(1)).saveFormData(ArgumentMatchers.eq(KeystoreKeys.useRegistrationAddressKey), any())(any(), any())
      }

      "NOT save the business registration address" in {
        verify(stateService, times(0)).saveFormData(ArgumentMatchers.eq(KeystoreKeys.correspondenceAddressKey), any())(any(), any())
      }
    }

  }
}
