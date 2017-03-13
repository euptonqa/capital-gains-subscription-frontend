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

import common.Constants.AffinityGroup
import common.Keys
import config.AppConfig
import data.TestUserBuilder
import models.{AuthorisationDataModel, Enrolment, Identifier}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.inject.Injector
import play.api.test.FakeRequest
import services.AuthorisationService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength, PayeAccount}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class AgentVisibilityPredicateSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  def mockedService(authorisationDataModel: Option[AuthorisationDataModel],
                    affinityGroup: String = "Agent"): AuthorisationService = {
    val mockService = mock[AuthorisationService]

    when(mockService.getAffinityGroup(ArgumentMatchers.any())).thenReturn(Future.successful(Some(affinityGroup)))

    mockService
  }

  "Calling the AgentVisibilityPredicate when supplied with appropriate URIs" should {
    val injector: Injector = app.injector

    def appConfig: AppConfig = injector.instanceOf[AppConfig]

    implicit val fakeRequest = FakeRequest()

    def predicate(dataModel: Option[AuthorisationDataModel], affinityGroup: String): AgentVisibilityPredicate =
      new AgentVisibilityPredicate(appConfig, mockedService(dataModel, affinityGroup))(affinityGroup)

    "return true for page visibility when the conditions of the predicate are satisfied" in {
      val authorisationDataModel = AuthorisationDataModel(CredentialStrength.Strong,
        AffinityGroup.Agent,
        ConfidenceLevel.L50,
        "example.com",
        Accounts())

      lazy val authContext = TestUserBuilder.noCredUserAuthContext
      val result = predicate(Some(authorisationDataModel), "Agent")(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "return false for page visibility when predicate conditions are not satisfied" in {
      val authorisationDataModel = AuthorisationDataModel(CredentialStrength.Strong,
        AffinityGroup.Organisation,
        ConfidenceLevel.L50,
        "example.com",
        Accounts())

      lazy val authContext = TestUserBuilder.noCredUserAuthContext
      val result = predicate(Some(authorisationDataModel), "Organisation")(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }
}
