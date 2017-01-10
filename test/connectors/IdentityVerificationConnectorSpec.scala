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

package connectors

import enums.IdentityVerificationResult
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.http.Status._
import play.api.libs.json.JsString
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class IdentityVerificationConnectorSpec extends UnitSpec with MockitoSugar {

  implicit val hc = mock[HeaderCarrier]

  def mockConnector(response: String): IdentityVerificationConnector = {
    val mockHttp = mock[HttpGet]
    val result = JsString(response)

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
      Future.successful(HttpResponse(OK, Some(result))))

    new IdentityVerificationConnector {

      override def http: HttpGet = mockHttp

      override val serviceUrl: String = ""
    }
  }

  "Calling identity verification response" should {

    "return a valid response when returning a Success" in {
      val connector = mockConnector("Success")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.Success
    }

    "return a valid response when returning an Incomplete response" in {
      val connector = mockConnector("Incomplete")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.Incomplete
    }

    "return a valid response when returning a Failed Matching response" in {
      val connector = mockConnector("FailedMatching")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.FailedMatching
    }

    "return a valid response when returning an Insufficient Evidence response" in {
      val connector = mockConnector("InsufficientEvidence")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.InsufficientEvidence
    }

    "return a valid response when returning a Locked Out response" in {
      val connector = mockConnector("LockedOut")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.LockedOut
    }

    "return a valid response when returning an User Aborted response" in {
      val connector = mockConnector("UserAborted")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.UserAborted
    }

    "return a valid response when returning a Timeout response" in {
      val connector = mockConnector("Timeout")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.Timeout
    }

    "return a valid response when returning a Technical Issue response" in {
      val connector = mockConnector("TechnicalIssue")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.TechnicalIssue
    }

    "return a valid response when returning a Precondition Failed response" in {
      val connector = mockConnector("PreconditionFailed")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.PreconditionFailed
    }

    "return a valid response when returning a Failed IV response" in {
      val connector = mockConnector("FailedIV")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.FailedIV
    }

    "return an unknown response when provided with a non recognised response" in {
      val connector = mockConnector("test")
      val result = connector.identityVerificationResponse("")

      await(result) shouldBe IdentityVerificationResult.UnknownOutcome
    }
  }
}
