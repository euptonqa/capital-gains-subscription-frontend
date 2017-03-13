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
import common.Constants.AffinityGroup
import common.Keys
import config.WSHttp
import connectors.AuthorisationConnector
import data.TestUserBuilder
import helpers.EnrolmentToCGTCheck
import models.{AuthorisationDataModel, Enrolment}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.inject.Injector
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuthorisationService
import traits.ControllerTestSpec
import auth.AuthenticatedNROrganisationAction
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength}

import scala.concurrent.Future

class CompanyControllerSpec extends ControllerTestSpec {

  val testOnlyUnauthorisedLoginUri = "just-a-test"

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.strongUserAuthContext): AuthorisedActions = {

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
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  def mockAuthorisationService(enrolmentsResponse: Option[Seq[Enrolment]], authResponse: Option[AuthorisationDataModel]): AuthorisationService ={
    implicit val mockHttp = mock[WSHttp]

    val mockConnector = mock[AuthorisationConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any())).thenReturn(Future.successful(authResponse))

    when(mockConnector.getEnrolmentsResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(enrolmentsResponse)

    new AuthorisationService(mockConnector)
  }

  "Calling .registerCompany" when {

    val authorisationDataModelPass = Some(AuthorisationDataModel(CredentialStrength.Weak, AffinityGroup.Organisation,
      ConfidenceLevel.L50, "example.com", Accounts()))

    lazy val fakeRequest = FakeRequest("GET", "/")
    lazy val action = createMockActions(valid = true)

    "the company is authorised and unenrolled" should {
      val enrolments = Option(Seq(Enrolment("key", Seq(), "")))
      lazy val authService = mockAuthorisationService(enrolments, authorisationDataModelPass)
      lazy val companyController: CompanyController = new CompanyController(mockConfig, action, authService)
      lazy val result = await(companyController.subscribe(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the business customer frontend" in {
        redirectLocation(result).get.toString shouldBe "http://localhost:9923/business-customer/business-verification/capital-gains-tax"
      }
    }
    "the company is authorised and enrolled" should {
      lazy val enrolments = Option(Seq(Enrolment(Keys.cgtCompanyEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), "")))
      lazy val authService = mockAuthorisationService(enrolments, authorisationDataModelPass)
      lazy val companyController: CompanyController = new CompanyController(mockConfig, action, authService)
      lazy val result = await(companyController.subscribe(fakeRequest))
      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the iForm" in {
        redirectLocation(result).get.toString shouldBe ""
      }
    }
    }

    "the company is unauthorised" should {
      val authorisationDataModelFail = Some(AuthorisationDataModel(CredentialStrength.None, AffinityGroup.Organisation,
        ConfidenceLevel.L0, "example.com", Accounts()))
      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions()
      val enrolments = Option(Seq(Enrolment("key", Seq(), "")))
      lazy val authService = mockAuthorisationService(enrolments, authorisationDataModelFail)

      lazy val companyController: CompanyController = new CompanyController(mockConfig, action, authService)
      lazy val result = await(companyController.subscribe(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test route" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }

}
