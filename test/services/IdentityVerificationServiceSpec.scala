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

import common.IdentityVerificationResult
import common.IdentityVerificationResult._
import connectors.IdentityVerificationConnector
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.Mockito._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class IdentityVerificationServiceSpec extends UnitSpec with MockitoSugar{

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  def mockedService(response: IdentityVerificationResult): IdentityVerificationService = {

    val mockConnector = mock[IdentityVerificationConnector]

    when(mockConnector.identityVerificationResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new IdentityVerificationService(mockConnector)
  }

  "Calling the getIdentityVerificationResult" should {

    "return a Success when Success is given" in {
      val target = mockedService(IdentityVerificationResult.Success)
      val result = target.getIdentityVerificationResult("")

      await(result) shouldBe IdentityVerificationResult.Success
    }

    "return a Unknown Response when one is given" in {
      val target = mockedService(IdentityVerificationResult.UnknownOutcome)
      val result = target.getIdentityVerificationResult("")

      await(result) shouldBe IdentityVerificationResult.UnknownOutcome
    }
  }

}
