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

import akka.util.Timeout
import auth.{AuthenticatedIndividualAction, AuthorisedActions, CgtIndividual}
import common.Constants.AffinityGroup
import common.Constants.ErrorMessages._
import common.Keys
import config.WSHttp
import data.MessageLookup
import helpers.LogicHelpers
import org.jsoup.Jsoup
import connectors.{AuthorisationConnector, SubscriptionConnector}
import data.TestUserBuilder
import models.{AuthorisationDataModel, Enrolment, SubscriptionReference}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.redirectLocation
import services.{AuthorisationService, SubscriptionService}
import traits.ControllerTestSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain._

import scala.concurrent.Future

class NonResidentIndividualSubscriptionControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "some-url"

  implicit val timeout: Timeout = mock[Timeout]

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

  def createMockSubscriptionService(response: Option[String]): SubscriptionService = {
    implicit val mockHttp = mock[WSHttp]

    val mockConnector = mock[SubscriptionConnector]

    when(mockConnector.getSubscriptionResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response.map(SubscriptionReference(_))))

    when(mockConnector.getSubscriptionNonResidentNinoResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response.map(SubscriptionReference(_))))

    when(mockConnector.getSubscriptionResponseGhost(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response.map(SubscriptionReference(_))))

    new SubscriptionService(mockConnector)
  }

  def createMockAuthorisationService(enrolmentsResponse: Option[Seq[Enrolment]], authResponse: Option[AuthorisationDataModel]): AuthorisationService = {
    implicit val mockHttp = mock[WSHttp]

    val mockConnector = mock[AuthorisationConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any())).thenReturn(Future.successful(authResponse))

    when(mockConnector.getEnrolmentsResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(enrolmentsResponse)

    new AuthorisationService(mockConnector)
  }

  def mockLogicHelper(valid: Boolean): LogicHelpers = {
    val helper = mock[LogicHelpers]

    when(helper.bindAndValidateCallbackUrl(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(valid))

    helper
  }

  "Calling the .nonResidentIndividualSubscription action" when {

    val nino = TestUserBuilder.createRandomNino

    val authorisationDataModelPass = AuthorisationDataModel(CredentialStrength.Strong, AffinityGroup.Individual,
      ConfidenceLevel.L500, "example.com", Accounts(paye = Some(PayeAccount(s"/paye/$nino", Nino(nino)))))

    "provided with an invalid callback URL" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val mockActions = createMockActions(valid = true)
      val mockSubscriptionService = createMockSubscriptionService(Some("eee"))
      val enrolments = Seq(Enrolment(Keys.cgtIndividualEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), ""))
      lazy val mockAuthorisationService = createMockAuthorisationService(Some(enrolments), Some(authorisationDataModelPass))

      lazy val target = new NonResidentIndividualSubscriptionController(mockActions, mockConfig, mockSubscriptionService,
        mockAuthorisationService, mockLogicHelper(false), messagesApi)

      lazy val result = target.nonResidentIndividualSubscription("http://www.google.com")(fakeRequest)
      lazy val body = Jsoup.parse(bodyOf(result))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "redirect to the Bad Request error page" in {
        body.title() shouldBe MessageLookup.Common.badRequest
      }
    }

    "provided with a valid user who has a nino and the user is already subscribed and a callback url is supplied" should {

      val fakeRequest = FakeRequest("GET", "/")
      lazy val mockActions = createMockActions(valid = true)
      val mockSubscriptionService = createMockSubscriptionService(Some("eee"))
      val enrolments = Seq(Enrolment(Keys.cgtIndividualEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), ""))
      lazy val mockAuthorisationService = createMockAuthorisationService(Some(enrolments), Some(authorisationDataModelPass))

      lazy val target = new NonResidentIndividualSubscriptionController(mockActions, mockConfig, mockSubscriptionService,
        mockAuthorisationService, mockLogicHelper(true), messagesApi)

      lazy val result = target.nonResidentIndividualSubscription("/test/route")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the I-form screen" in {
        redirectLocation(result).get.toString shouldBe "/test/route"
      }
    }

    "provided with a valid user who has a nino and the user is not yet subscribed" should {

      val fakeRequest = FakeRequest("GET", "/")
      lazy val mockActions = createMockActions(valid = true)
      val mockSubscriptionService = createMockSubscriptionService(Some("eee"))
      val enrolments = Seq(Enrolment("anotherKey", Seq(), ""), Enrolment("key", Seq(), ""))
      lazy val mockAuthorisationService = createMockAuthorisationService(Some(enrolments), Some(authorisationDataModelPass))

      lazy val target = new NonResidentIndividualSubscriptionController(mockActions, mockConfig, mockSubscriptionService,
        mockAuthorisationService, mockLogicHelper(true), messagesApi)

      lazy val result = target.nonResidentIndividualSubscription("/test/route")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the confirmation of subscription screen" in {
        redirectLocation(result).get.toString shouldBe Some(routes.CGTSubscriptionController.confirmationOfSubscription("eee")).get.url
      }
    }

    "provided with a valid user who has a nino but subscription fails" should {

      val fakeRequest = FakeRequest("GET", "/")
      lazy val mockActions = createMockActions(valid = true)
      val mockSubscriptionService = createMockSubscriptionService(None)
      val enrolments = Seq(Enrolment("otherKey", Seq(), ""), Enrolment("key", Seq(), ""))
      lazy val mockAuthorisationService = createMockAuthorisationService(Some(enrolments), Some(authorisationDataModelPass))

      lazy val target = new NonResidentIndividualSubscriptionController(mockActions, mockConfig, mockSubscriptionService,
        mockAuthorisationService, mockLogicHelper(true), messagesApi)

      lazy val ex = intercept[Exception] {
        await(target.nonResidentIndividualSubscription("/test/route")(fakeRequest))
      }

      s"return an Exception with text $failedToEnrolIndividual" in {
        ex.getMessage shouldEqual failedToEnrolIndividual
      }
    }
  }

  "Calling the .notEnrolled method" when {

    val authorisationDataModelNoNino = AuthorisationDataModel(CredentialStrength.Strong, AffinityGroup.Individual,
      ConfidenceLevel.L500, "example.com", Accounts())

    "provided with a user who has no nino" should {

      val fakeRequest = FakeRequest("GET", "/")
      lazy val mockActions = createMockActions(valid = true, TestUserBuilder.create200ConfidenceUserAuthContext)
      val mockSubscriptionService = createMockSubscriptionService(Some("eee"))
      val enrolments = Seq(Enrolment("otherKey", Seq(), ""), Enrolment("key", Seq(), ""))
      lazy val mockAuthorisationService = createMockAuthorisationService(Some(enrolments), Some(authorisationDataModelNoNino))

      lazy val target = new NonResidentIndividualSubscriptionController(mockActions, mockConfig, mockSubscriptionService,
        mockAuthorisationService, mockLogicHelper(true), messagesApi)

      lazy val result = target.nonResidentIndividualSubscription("/test/route")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the collect user details screen" in {
        redirectLocation(result).get.toString shouldBe Some(routes.UserDetailsController.userDetails()).get.url
      }
    }
  }
}
