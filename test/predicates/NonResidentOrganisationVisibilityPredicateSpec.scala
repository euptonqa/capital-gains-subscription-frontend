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
import models.AuthorisationDataModel
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.test.FakeRequest
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class NonResidentOrganisationVisibilityPredicateSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def mockedService(authorisationDataModel: Option[AuthorisationDataModel],
                    affinityGroup: String): AuthorisationService = {

    val mockService = mock[AuthorisationService]

    when(mockService.getAffinityGroup(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(affinityGroup)))

    mockService
  }

  def predicate(dataModel: Option[AuthorisationDataModel], affinityGroup: String): NonResidentOrganisationVisibilityPredicate = {
    new NonResidentOrganisationVisibilityPredicate(mockedService(dataModel, affinityGroup))(affinityGroup)
  }

  "Calling the VisibilityPredicate" when {

    implicit val fakeRequest = FakeRequest()
    implicit val hc = HeaderCarrier

    "supplied with valid AuthContext" should {

      val authorisationDataModel = AuthorisationDataModel(CredentialStrength.None,
                                    AffinityGroup.Organisation,
                                    ConfidenceLevel.L50,
                                    "example.com",
                                    Accounts())

      lazy val authContext = TestUserBuilder.noCredUserAuthContext
      lazy val result = predicate(Some(authorisationDataModel), "Organisation")(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      "return true" in {
        pageVisibility.isVisible shouldBe true
      }
    }

    "supplied with invalid AuthContext" should {

      val authorisationDataModel = AuthorisationDataModel(CredentialStrength.None,
                                    AffinityGroup.Individual,
                                    ConfidenceLevel.L50,
                                    "example.com",
                                    Accounts())

      lazy val authContext = TestUserBuilder.noCredUserAuthContext
      lazy val result = predicate(Some(authorisationDataModel), "Individual")(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      "return false" in {
        pageVisibility.isVisible shouldBe false
      }
    }
  }
}
