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

import java.net.URI

import common.Constants.AffinityGroup._
import connectors.AuthorisationConnector
import data.TestUserBuilder
import models.AuthorisationDataModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.test.FakeRequest
import services.AuthorisationService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class AffinityGroupAgentPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  def mockedService(response: Option[AuthorisationDataModel], nino: Option[Nino]): AuthorisationService = {

    val mockConnector = mock[AuthorisationConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new AuthorisationService(mockConnector)
  }

  val dummyUri = new URI("http://example.com")
  implicit val hc = mock[HeaderCarrier]

  "Instantiating AffinityGroupIndividualPredicate" when {

    "supplied with an authContext with an agent credential should return true for page visibility" in {

      val service = mockedService(Some(AuthorisationDataModel(CredentialStrength.Strong, Agent, ConfidenceLevel.L200, "", Accounts())), None)

      val predicate = new AffinityGroupAgentPredicate(service)(dummyUri)
      val authContext = TestUserBuilder.weakUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "supplied with an authContext with a organisation credential should return false for page visibility" in {

      val service = mockedService(Some(AuthorisationDataModel(CredentialStrength.Strong, Organisation, ConfidenceLevel.L200, "", Accounts())), None)

      val predicate = new AffinityGroupAgentPredicate(service)(dummyUri)
      val authContext = TestUserBuilder.weakUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }

    "supplied with an authContext with an individual credential should return false for page visibility" in {

      val service = mockedService(Some(AuthorisationDataModel(CredentialStrength.Strong, Individual, ConfidenceLevel.L200, "", Accounts())), None)

      val predicate = new AffinityGroupAgentPredicate(service)(dummyUri)
      val authContext = TestUserBuilder.noCredUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }

}
