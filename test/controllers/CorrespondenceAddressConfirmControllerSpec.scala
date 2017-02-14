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
import assets.MessageLookup.{UseRegisteredAddress => messages}
import auth.{AuthorisedActions, CgtNROrganisation}
import connectors.KeystoreConnector
import forms.YesNoForm
import models.{CompanyAddressModel, ReviewDetails, YesNoModel}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import types.AuthenticatedNROrganisationAction
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class CorrespondenceAddressConfirmControllerSpec extends ControllerTestSpec {

  object TestData {
    val businessAddress = CompanyAddressModel(addressLine1 = None,
      addressLine2 = None,
      addressLine3 = None,
      addressLine4 = None,
      postCode = None,
      country = None)

    val businessDetails = ReviewDetails(businessName = "",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "",
      businessType = None,
      agentReferenceNumber = None)
  }

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

  "Calling .correspondenceAddressConfirm" when {

    "no details for the business exist in keystore" should {

      "return a return a 500 response code" in {

        val request = FakeRequest("", "")
        val stateService = mock[KeystoreConnector]
        val actions = createMockActions(true)
        val form = new YesNoForm(messagesApi)
        val target = new CorrespondenceAddressConfirmController(mockConfig, messagesApi, stateService, actions, form)

        when(stateService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(None))
        when(stateService.fetchAndGetFormData[YesNoModel](anyString())(any(), any())).thenReturn(Future.successful(None))

        val result = target.correspondenceAddressConfirm(request)

        status(result) shouldBe 500
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

  }

}
