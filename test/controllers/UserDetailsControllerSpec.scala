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
import data.{MessageLookup, TestUserBuilder}
import forms.UserFactsForm
import models.{SubscriptionReference, UserFactsModel}
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
import auth.AuthenticatedIndividualAction
import helpers.CountryHelper
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.util.Failure

class UserDetailsControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "some-url"
  val form: UserFactsForm = app.injector.instanceOf[UserFactsForm]
  val countryHelper: CountryHelper = app.injector.instanceOf[CountryHelper]

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithNINO): AuthorisedActions = {

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
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  def createMockService(cgtRef: String): SubscriptionService = {
    val mockService = mock[SubscriptionService]

    when(mockService.getSubscriptionResponseGhost(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(SubscriptionReference(cgtRef))))

    mockService
  }

  "Calling .userDetails" when {
    val service = createMockService("CGT123456")

    "using correct authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
      lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper)
      lazy val result = controller.userDetails(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "display the user details view" in {
        document.title() shouldBe MessageLookup.UserDetails.title
      }
    }

    "using incorrect authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val actions = createMockActions(valid = false, TestUserBuilder.noCredUserAuthContext)
      lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper)
      lazy val result = controller.userDetails(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the correct error page" in {
        redirectLocation(result) shouldBe Some("some-url")
      }
    }
  }

  "Calling .submitUserDetails" should {
    val service = createMockService("CGT123456")
    val fakeRequest = FakeRequest("POST", "/")
      .withFormUrlEncodedBody()

    lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
    lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper)
    lazy val result = controller.submitUserDetails(fakeRequest)
    lazy val document = Jsoup.parse(bodyOf(result))

    "using an invalid form" should {

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "return to the user details page" in {
        document.title() shouldBe MessageLookup.UserDetails.title
      }
    }

    "using a valid form with correct response" should {
      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody("firstName" -> "Name", "lastName" -> "Surname", "addressLineOne" -> "LineOne",
          "addressLineTwo" -> "LineTwo", "townOrCity" -> "City", "county" -> "County", "postCode" -> "Postcode", "country" -> "Country")

      lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
      lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper)
      lazy val result = controller.submitUserDetails(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "display the cgt reference page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.CGTSubscriptionController.confirmationOfSubscriptionNonResIndv("CGT123456").toString)
      }
    }

    "using a valid form with an incorrect response" should {
      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody("firstName" -> "Name", "lastName" -> "Surname", "addressLineOne" -> "LineOne",
          "addressLineTwo" -> "LineTwo", "townOrCity" -> "", "county" -> "", "postCode" -> "", "country" -> "Country")

      lazy val actions = createMockActions(valid = true, TestUserBuilder.strongUserAuthContext)
      lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper) {
        val mockException: Exception = mock[Exception]
        when(mockException.getMessage)
          .thenReturn("test")

        override def subscribeUser(fullDetailsModel: UserFactsModel)
                                  (implicit hc: HeaderCarrier): Future[scala.util.Try[String]] = Future.successful(Failure(mockException))
      }

      lazy val ex = intercept[Exception] {
        await(controller.submitUserDetails(fakeRequest))
      }

      s"throw an exception with text 'test'" in {
        ex.getMessage shouldEqual "test"
      }
    }

    "using incorrect authorisation" should {
      val fakeRequest = FakeRequest("POST", "/")
      lazy val actions = createMockActions(valid = false, TestUserBuilder.noCredUserAuthContext)
      lazy val controller = new UserDetailsController(mockConfig, form, messagesApi, actions, service, countryHelper)
      lazy val result = controller.submitUserDetails(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the correct error page" in {
        redirectLocation(result) shouldBe Some("some-url")
      }
    }
  }
}
