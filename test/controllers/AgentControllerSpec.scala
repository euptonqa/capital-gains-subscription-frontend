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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import types._
import uk.gov.hmrc.play.frontend.auth.AuthContext

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

  "Calling .registerAgent" when {

    "the agent is authorised" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val action = createMockActions(valid = true)
      lazy val agentController: AgentController = new AgentController(mockConfig, action)
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
      lazy val agentController: AgentController = new AgentController(mockConfig, action)
      lazy val result = await(agentController.agent(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test route" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }
  }
}
