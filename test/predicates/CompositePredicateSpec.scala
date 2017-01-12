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
import com.google.inject.{Inject, Singleton}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import config.AppConfig
import connectors.AuthorisationConnector
import models.AuthorisationDataModel
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
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future


@Singleton
class CompositePredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar{

  def mockedService(response: Option[AuthorisationDataModel]): AuthorisationService = {

    val mockConnector = mock[AuthorisationConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new AuthorisationService(mockConnector)
  }

  "Calling the CompositePredicate when supplied with appropriate URIs" should {
    val injector: Injector = fakeApplication.injector
    def appConfig: AppConfig = injector.instanceOf[AppConfig]

    val postSignURI = "http://post-sigin-example.com"
    val notAuthorisedRedirectURI = "http://not-authorised-example.com"
    val ivUpliftURI = appConfig.ivUpliftUrl
    val twoFactorURI = appConfig.twoFactorUrl
    val authorisationURI = "http://authorisation-uri-example.com"

    implicit val fakeRequest = FakeRequest()

    val ninoPass = TestUserBuilder.createRandomNino

    val authorisationDataModelPass = new AuthorisationDataModel(CredentialStrength.Strong, AffinityGroup.Individual,
      ConfidenceLevel.L500, "example.com", Accounts(paye = Some(PayeAccount(s"/paye/$ninoPass", Nino(ninoPass)))))
    val authorisationDataModelFail = new AuthorisationDataModel(CredentialStrength.None, AffinityGroup.Organisation,
      ConfidenceLevel.L50, "example.com", Accounts())

    implicit val hc = HeaderCarrier()

    def predicate(dataModel: Option[AuthorisationDataModel]): CompositePredicate = new CompositePredicate(appConfig, mockedService(dataModel))(postSignURI,
      notAuthorisedRedirectURI,
      ivUpliftURI,
      twoFactorURI,
      authorisationURI)(hc)

    "return true for page visibility when the relevant predicates are given an AuthContext that meets their respective conditions" in {
      val authContext = TestUserBuilder.compositePredicateUserPass
      val result = predicate(Some(authorisationDataModelPass))(authContext, fakeRequest)

      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "return false for page visibility when the relevant predicates are given an AuthContext that fails to meet their respective conditions" in {
      val authContext = TestUserBuilder.compositePredicateUserFail
      val result = predicate(Some(authorisationDataModelFail))(authContext, fakeRequest)

      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }

}
