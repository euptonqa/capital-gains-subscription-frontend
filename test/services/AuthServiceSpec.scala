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

package services

import connectors.AuthConnector
import models.AuthDataModel
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.mockito.ArgumentMatchers
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class AuthServiceSpec extends UnitSpec with MockitoSugar {

  implicit val hc = mock[HeaderCarrier]

  def mockedService(response: Option[AuthDataModel]): AuthService = {

    val mockConnector = mock[AuthConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new AuthService {
      override val authConnector: AuthConnector = mockConnector
    }
  }

  "Calling AuthServiceSpec .getAuthData" should {

    "return an AuthDataModel with a valid request" in {
      val service = mockedService(Some(AuthDataModel(CredentialStrength.Strong, "", ConfidenceLevel.L200, "", "")))
      val result = service.getAuthDataModel(hc)
      await(result) shouldBe Some(AuthDataModel(CredentialStrength.Strong, "", ConfidenceLevel.L200, "", ""))
    }

    "return a None with an invalid request" in {
      val service = mockedService(None)
      val result = service.getAuthDataModel(hc)
      await(result) shouldBe None
    }
  }
}
