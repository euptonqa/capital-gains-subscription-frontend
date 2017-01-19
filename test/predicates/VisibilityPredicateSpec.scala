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
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import config.AppConfig
import models.{AuthorisationDataModel, Enrolment, Identifier}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.inject.Injector
import services.AuthorisationService
import uk.gov.hmrc.domain.Nino
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength, PayeAccount}
import common.Constants.AffinityGroup
import common.Keys
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future


class VisibilityPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar{

  def mockedService(authorisationDataModel: Option[AuthorisationDataModel], enrolments: Option[Seq[Enrolment]],
                    enrolmentUri: String = "http://enrolments-uri.com",
                    affinityGroup: String = "Individual"): AuthorisationService = {

    val mockService = mock[AuthorisationService]

    when(mockService.getAuthDataModel(ArgumentMatchers.any()))
      .thenReturn(Future.successful(authorisationDataModel))

    when(mockService.getAffinityGroup(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(affinityGroup)))

    when(mockService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future.successful(enrolments))

    mockService
  }

  "Calling the VisibilityPredicate when supplied with appropriate URIs" should {
    val injector: Injector = fakeApplication.injector
    def appConfig: AppConfig = injector.instanceOf[AppConfig]

    val postSignURI = "http://post-sigin-example.com"
    val notAuthorisedRedirectURI = "http://not-authorised-example.com"
    val ivUpliftURI = appConfig.ivUpliftUrl
    val twoFactorURI = appConfig.twoFactorUrl
    val authorisationURI = "http://authorisation-uri-example.com"
    val enrolmentURI = "http://sample-enrolment-uri.com"

    implicit val fakeRequest = FakeRequest()

    val ninoPass = TestUserBuilder.createRandomNino

    val authorisationDataModelPass = AuthorisationDataModel(CredentialStrength.Strong, AffinityGroup.Individual,
      ConfidenceLevel.L500, "example.com", Accounts(paye = Some(PayeAccount(s"/paye/$ninoPass", Nino(ninoPass)))))
    val authorisationDataModelFail = AuthorisationDataModel(CredentialStrength.None, AffinityGroup.Organisation,
      ConfidenceLevel.L50, "example.com", Accounts())

    val enrolmentsPass = Seq(Enrolment(Keys.cGTEnrolmentKey, Seq(Identifier("test","test")), ""), Enrolment("key", Seq(), ""))
    val enrolmentsFail = Seq(Enrolment("otherKey", Seq(), ""), Enrolment("key", Seq(), ""))

    implicit val hc = HeaderCarrier()

    def predicate(dataModel: Option[AuthorisationDataModel], enrolments: Option[Seq[Enrolment]]): VisibilityPredicate =
      new VisibilityPredicate(appConfig, mockedService(dataModel, enrolments))(postSignURI,
      notAuthorisedRedirectURI,
      ivUpliftURI,
      twoFactorURI,
      authorisationURI,
      enrolmentURI)

    "return true for page visibility when the relevant predicates are given an AuthContext that meets their respective conditions" in {
      lazy val authContext = TestUserBuilder.visibilityPredicateUserPass
      lazy val result = predicate(Some(authorisationDataModelPass), Some(enrolmentsPass))(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "return false for page visibility when the relevant predicates are given an AuthContext that fails to meet their respective conditions" in {
      lazy val authContext = TestUserBuilder.visibilityPredicateUserFail
      lazy val result = predicate(Some(authorisationDataModelFail), Some(enrolmentsFail))(authContext, fakeRequest)

      lazy val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }

}
