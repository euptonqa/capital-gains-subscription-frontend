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

import java.time.LocalDate

import data.MessageLookup.{AgentConfirmation => messages}
import auth.{AuthorisedActions, CgtAgent}
import common.Constants.AffinityGroup
import common.{Dates, Keys}
import config.WSHttp
import connectors._
import data.{MessageLookup, TestUserBuilder}
import helpers.EnrolmentToCGTCheck
import models._
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.inject.Injector
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AgentService, AuthorisationService, SubscriptionService}
import traits.ControllerTestSpec
import auth._
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class AgentControllerSpec extends ControllerTestSpec {

  val testOnlyUnauthorisedLoginUri = "just-a-test"

  object TestData {
    val businessAddress = Address(line_1 = "",
      line_2 = "",
      line_3 = None,
      line_4 = None,
      country = "")

    val validBusinessDetails = ReviewDetails(businessName = "Agency 1234",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "SAP number",
      businessType = None,
      agentReferenceNumber = Some("ARN123456"))

    val invalidBusinessDetails = ReviewDetails(businessName = "Agency 1234",
      businessAddress = businessAddress,
      sapNumber = "",
      safeId = "SAP number",
      businessType = None,
      agentReferenceNumber = None)
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

  def setupController(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.strongUserAuthContext,
                      businessDetails: Option[ReviewDetails] = Some(TestData.validBusinessDetails),
                      enrolmentResponse: AgentEnrolmentResponse = SuccessAgentEnrolmentResponse,
                      enrolmentsResponse: Option[Seq[Enrolment]] = None,
                      authResponse: Option[AuthorisationDataModel] = None): AgentController = {

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

    val sessionService = mock[KeystoreConnector]
    val service = mock[AgentService]

    val mockSubscriptionService = createMockSubscriptionService(Some("eee"))
    val mockAuthorisationService = createMockAuthorisationService(enrolmentsResponse, authResponse)

    when(sessionService.fetchAndGetBusinessData()(any())).thenReturn(Future.successful(businessDetails))
    when(service.getAgentEnrolmentResponse(any())(any())).thenReturn(Future.successful(enrolmentResponse))

    new AgentController(mockConfig, mockActions, service, sessionService, mockAuthorisationService, mockSubscriptionService, messagesApi)

  }


  "Calling .agent" when {

    val authorisationDataModelPass = Some(AuthorisationDataModel(CredentialStrength.Weak, AffinityGroup.Agent,
      ConfidenceLevel.L50, "example.com", Accounts()))

    "the agent is authorised and enrolled" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      val enrolments = Option(Seq(Enrolment(Keys.cgtAgentEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), "")))
      lazy val agentController = setupController(valid = true, enrolmentsResponse = enrolments, authResponse = authorisationDataModelPass)

      lazy val result = await(agentController.agent(fakeRequest))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the iForm page" in {
        redirectLocation(result).get.toString shouldBe "http://www.gov.uk"
      }
    }


    "the agent is authorised and not enrolled" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      val enrolments = Option(Seq(Enrolment("other key", Seq(), ""), Enrolment("key", Seq(), "")))
      lazy val agentController = setupController(valid = true, enrolmentsResponse = enrolments, authResponse = authorisationDataModelPass)

      lazy val result = await(agentController.agent(fakeRequest))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the setupYourAgency page" in {
        document.title() shouldBe MessageLookup.SetupYourAgency.title
      }
    }


    "the agent is unauthorised" should {
      lazy val fakeRequest = FakeRequest("GET", "/")
      val enrolments = Some(Seq(Enrolment(Keys.cgtAgentEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), "")))
      lazy val agentController = setupController(enrolmentsResponse = enrolments, authResponse = authorisationDataModelPass)

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
      lazy val agentController = setupController()
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
      lazy val agentController = setupController(valid = true, businessDetails = None)
      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 500 response code" in {
        status(result) shouldBe 500
      }
    }

    "no ARN is returned from keystore" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val agentController = setupController(valid = true, businessDetails = Some(TestData.invalidBusinessDetails))
      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 500 response code" in {
        status(result) shouldBe 500
      }
    }

    "supplied with a valid request" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val agentController = setupController(valid = true)
      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 200 response code" in {
        status(result) shouldBe 200
      }

      "load the correct view" which {

        lazy val document = Jsoup.parse(bodyOf(result))
        lazy val greenBanner = document.select("div #confirmation-banner")

        s"has the title ${messages.title}" in {
          document.title shouldEqual messages.title
        }

        s"contains the business name from business details in the text '${messages.setUp} Agency 1234 ${messages.forCgt}'" in {
          lazy val p1 = greenBanner.select("p").get(0)
          p1.select("strong").text shouldEqual s"${messages.setUp} Agency 1234 ${messages.forCgt}"
        }

        "contains the current date" in {
          lazy val now = LocalDate.now()
          lazy val nowFormatted = Dates.dateSummaryFormat.format(now)
          lazy val p2 = greenBanner.select("p").get(1)
          p2.select("strong").text shouldEqual s"on $nowFormatted"
        }

        s"contains the ARN from business details in the text '${messages.yourArn} ARN123456'" in {
          lazy val p3 = greenBanner.select("p").get(2)
          p3.select("strong").text shouldEqual s"${messages.yourArn} ARN123456"
        }
      }
    }

    "a FailedAgentEnrolmentResponse is returned from the agent service" should {

      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val agentController = setupController(valid = true, enrolmentResponse = FailedAgentEnrolmentResponse)
      lazy val result = await(agentController.registeredAgent(fakeRequest))

      "return a 500 response code" in {
        status(result) shouldBe 500
      }
    }
  }

  "Calling .handleBusinessData" when {

    implicit val hc: HeaderCarrier = mock[HeaderCarrier]

    "no details for the business exist" should {

      lazy val agentController = setupController(valid = true, businessDetails = None)

      "throw an Exception with text 'Failed to retrieve registration details from BusinessCustomer keystore'" in {
        lazy val ex = intercept[Exception] {
          await(agentController.handleBusinessData())
        }

        ex.getMessage shouldBe "Failed to retrieve registration details from BusinessCustomer keystore"
      }
    }

    "details for the business exist" should {

      lazy val agentController = setupController(valid = true)
      lazy val result = await(agentController.handleBusinessData())

      "return a review details model" in {
        result shouldEqual TestData.validBusinessDetails
      }
    }
  }

  "Calling .constructAgentSubmissionModel" when {

    "ARN is not defined" should {

      lazy val agentController = setupController(valid = true)

      "throw an Exception with text 'Agent Details retrieved did not contain an ARN'" in {
        lazy val ex = intercept[Exception] {
          await(agentController.constructAgentSubmissionModel(TestData.invalidBusinessDetails))
        }

        ex.getMessage shouldBe "Agent Details retrieved did not contain an ARN"
      }
    }

    "ARN is defined" should {

      lazy val agentController = setupController(valid = true)
      lazy val result = await(agentController.constructAgentSubmissionModel(TestData.validBusinessDetails))

      "return an agent submission model" in {
        result shouldEqual AgentSubmissionModel("SAP number", "ARN123456")
      }
    }
  }
}
