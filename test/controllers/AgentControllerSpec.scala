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
import auth.{AuthorisedActions, CgtAgent, CgtNROrganisation}
import builders.TestUserBuilder
import connectors.{KeystoreConnector, SuccessAgentEnrolmentResponse}
import models.{Address, ReviewDetails}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AgentService
import types._
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class AgentControllerSpec extends ControllerTestSpec {

  val testOnlyUnauthorisedLoginUri = "just-a-test"

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.strongUserAuthContext): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedAgentAction](0)
            val agent = CgtAgent(authContext)
            Action.async(action(agent))
          }
        })
    }
    else {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    mockActions
  }

  object TestData {
    val businessAddress = Address(line_1 = "",
      line_2 = "",
      line_3 = None,
      line_4 = None,
      country = "")

    val validBusinessDetails = ReviewDetails(businessName = "Agency Name",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "SAP number",
      businessType = None,
      agentReferenceNumber = Some("ARN123456"))

    val invalidBusinessDetails = ReviewDetails(businessName = "Agency Name",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "SAP number",
      businessType = None,
      agentReferenceNumber = None)
  }

  "Calling .agent" when {

    "the agent is authorised" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions(valid = true)
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)
      lazy val result = await(agentController.agent(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the business customer frontend" in {
        redirectLocation(result).get.toString shouldBe mockConfig.businessCompanyFrontendRegister
      }
    }

    "the agent is unauthorised" should {
      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions()
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)
      lazy val result = await(agentController.agent(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test route" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }
  }

  "Calling .registeredAgent" when {

    "the agent is not authorised" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions()
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)

      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a return a 303 response code" in {
        status(result) shouldBe 303
      }

      "redirect to the test route" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }

    "no details for the business exist in keystore" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions(valid = true)
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)

      when(sessionService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(None))

      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 500 response code" in {
        status(result) shouldBe 500
      }
    }

    "no ARN is returned from keystore" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions(valid = true)
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)

      when(sessionService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.invalidBusinessDetails)))

      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 500 response code" in {
        status(result) shouldBe 500
      }
    }

    "supplied with a valid request" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions(valid = true)
      lazy val sessionService = mock[KeystoreConnector]
      lazy val service = mock[AgentService]
      lazy val agentController: AgentController = new AgentController(mockConfig, action, service, sessionService, messagesApi)

      when(sessionService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(Some(TestData.validBusinessDetails)))
      when(service.getAgentEnrolmentResponse(any())(any())).thenReturn(Future.successful(SuccessAgentEnrolmentResponse))

      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 200 response code" in {
        status(result) shouldBe 200
      }

      "load the correct view" which {

        lazy val document = Jsoup.parse(bodyOf(result))

       
      }
    }
  }
}
