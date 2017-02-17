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

package predicates

import builders.TestUserBuilder
import common.Constants.AffinityGroup
import common.Keys
import config.AppConfig
import helpers.EnrolmentToCGTCheck
import models.{AuthorisationDataModel, Enrolment, Identifier}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import services.AuthorisationService
import org.mockito.Mockito._
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength, PayeAccount}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class AgentVisibilityPredicateSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def mockedService(authorisationDataModel: Option[AuthorisationDataModel], enrolments: Option[Seq[Enrolment]],
                   enrolmentUri: String = "http://enrolments-uri.com",
                    affinityGroup: String = "Agent"): AuthorisationService = {
    val mockService = mock[AuthorisationService]

    when(mockService.getAffinityGroup(ArgumentMatchers.any())).thenReturn(Future.successful(Some(affinityGroup)))

    when(mockService.getEnrolments(ArgumentMatchers.any())).thenReturn(Future.successful(enrolments))

    mockService
  }

  "Calling the AgentVisibilityPredicate when supplied with appropriate URIs" should {
    val injector: Injector = fakeApplication.injector
    def appConfig: AppConfig = injector.instanceOf[AppConfig]
    def enrolmentToCGTCheck: EnrolmentToCGTCheck = injector.instanceOf[EnrolmentToCGTCheck]

    val postSignUri = "http://post-sign-in-example.com"
    val notAuthorisedRedirectURI = "http://not-authorised-example.com"
    val twoFactorURI = appConfig.twoFactorUrl
    val enrolmentURI = "http://sample-enrolment-uri.com"
    val affinityGroup = "http://affinitygroup.com"

    implicit val fakeRequest = FakeRequest()

    val nino = TestUserBuilder.createRandomNino


    val authorisationDataModelPass = AuthorisationDataModel(CredentialStrength.Strong, AffinityGroup.Agent,
      ConfidenceLevel.L500, "example.com", Accounts(paye = Some(PayeAccount(s"/paye/$nino", Nino(nino)))))

    val authorisationDataModelFail = AuthorisationDataModel(CredentialStrength.None, AffinityGroup.Organisation,
      ConfidenceLevel.L50, "example.com", Accounts())

    val enrolmentsPass = Seq(Enrolment(Keys.cgtAgentEnrolmentKey, Seq(Identifier("test","test")), ""), Enrolment("key", Seq(), ""))
    val enrolmentsFail = Seq(Enrolment("otherKey", Seq(), ""), Enrolment("key", Seq(), ""))

    def predicate(dataModel: Option[AuthorisationDataModel], enrolments: Option[Seq[Enrolment]], affinityGroup: String): AgentVisibilityPredicate =
      new AgentVisibilityPredicate(appConfig, mockedService(dataModel, enrolments, affinityGroup), enrolmentToCGTCheck)(postSignUri,
        notAuthorisedRedirectURI,
        twoFactorURI,
        affinityGroup,
        enrolmentURI)

    "return true for page visibility when the conditions of the predicate are satisfied" in {
      lazy val authContext = TestUserBuilder.visibilityPredicateUserPass
      val result = predicate(Some(authorisationDataModelPass), Some(enrolmentsPass), authorisationDataModelPass.affinityGroup)(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "return false for page visibility when predicate conditions are not satisfied" in {
      lazy val authContext = TestUserBuilder.visibilityPredicateUserFail
      val result = predicate(Some(authorisationDataModelFail), Some(enrolmentsFail), authorisationDataModelFail.affinityGroup)(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }
}
