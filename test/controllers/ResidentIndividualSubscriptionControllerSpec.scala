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

import auth.{AuthorisedActions, CgtIndividual}
import config.AppConfig
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import types.AuthenticatedIndividualAction
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ResidentIndividualSubscriptionControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val unauthorisedLoginUri = "some-url"
  val mockConfig: AppConfig = mock[AppConfig]

  def createMockActions(valid: Boolean = false): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedResidentIndividualAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedIndividualAction](0)
            Action.async(action(mock[CgtIndividual]))
          }
        })
    }
    else {
      when(mockActions.authorisedResidentIndividualAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  "Calling .residentIndividualSubscription" when {

    "provided with a valid user" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val actions = createMockActions(true)

      lazy val target = new ResidentIndividualSubscriptionController(actions, mockConfig)
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
      lazy val actions = createMockActions()

      lazy val target = new ResidentIndividualSubscriptionController(actions, mockConfig)
      lazy val result = target.residentIndividualSubscription(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the mock gg-login page" in {
        redirectLocation(result) shouldBe Some(unauthorisedLoginUri)
      }
    }
  }
}
