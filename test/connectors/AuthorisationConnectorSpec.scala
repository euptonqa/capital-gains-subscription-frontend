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

import builders.TestUserBuilder
import common.Constants.AffinityGroup
import config.WSHttp
import models.{AuthorisationDataModel, Enrolment, Identifier}
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.http.Status._
import uk.gov.hmrc.play.frontend.auth.connectors.domain._

import scala.concurrent.Future


class AuthorisationConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication{

  val nino = TestUserBuilder.createRandomNino
  implicit val hc = HeaderCarrier()

  def affinityResponse(key: String, nino: String): JsValue = Json.parse(
    s"""{"uri":"/auth/oid/57e915480f00000f006d915b","confidenceLevel":200,"credentialStrength":"strong",
       |"userDetailsLink":"http://localhost:9978/user-details/id/000000000000000000000000","legacyOid":"00000000000000000000000",
       |"new-session":"/auth/oid/57e915480f00000f006d915b/session","ids":"/auth/oid/57e915480f00000f006d915b/ids",
       |"credentials":{"gatewayId":"000000000000000"},"accounts":{"paye":{"link":"test","nino":"$nino"}},"lastUpdated":"2016-09-26T12:32:08.734Z",
       |"loggedInAt":"2016-09-26T12:32:08.734Z","levelOfAssurance":"1","enrolments":"/auth/oid/00000000000000000000000/enrolments",
       |"affinityGroup":"$key","correlationId":"0000000000000000000000000000000000000000000000000000000000000000","credId":"000000000000000"}""".stripMargin
    )

  "AuthorisationConnector .getAuthResponse" should {

    lazy val mockHttp = mock[WSHttp]

    lazy val target = new AuthorisationConnector(mockHttp) {
      override lazy val serviceUrl: String = "localhost"
      override val authorityUri: String = "auth/authority"
    }

    "with a valid request" should {

      when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(OK, Some(affinityResponse("Individual", nino)))))

      val result = await(target.getAuthResponse()(hc)).get

      "return a valid AuthorisationDataModel type" in {
        result shouldBe a[AuthorisationDataModel]
      }

      "return an AuthorisationDataModel containing a confidence level of 200" in {
        result.confidenceLevel shouldBe ConfidenceLevel.L200
      }

      "return an AuthorisationDataModel containing a credential strength of Strong" in {
        result.credentialStrength shouldBe CredentialStrength.Strong
      }

      "return an AuthorisationDataModel containing a uri of /auth/oid/57e915480f00000f006d915b" in {
        result.uri shouldBe "/auth/oid/57e915480f00000f006d915b"
      }

      "return an AuthorisationDataModel containing an Affinity Group of Individual" in {
        result.affinityGroup shouldBe AffinityGroup.Individual
      }
    }

    "return a None with an invalid request" in {
      when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(affinityResponse("Individual", nino)))))
      await(target.getAuthResponse()(hc)) shouldBe None
    }
  }

  def setupConnector(jsonString: String, status: Int): AuthorisationConnector = {

    val mockHttp = mock[WSHttp]

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(Json.parse(jsonString)))))

    new AuthorisationConnector(mockHttp)
  }

  "Calling .getEnrolments" should {

    "return a None with a failed response" in {
      val target = setupConnector("[]", 500)
      val result = target.getEnrolmentsResponse("")

      await(result) shouldBe None
    }

    "return an empty sequence with an empty json response" in {
      val target = setupConnector("[]", 200)
      val result = target.getEnrolmentsResponse("")

      await(result) shouldBe Some(Seq())
    }

    "return a valid sequence with an single enrolment" in {
      val target = setupConnector("""[{"key":"key","identifiers":[],"state":"state"}]""", 200)
      val result = target.getEnrolmentsResponse("")

      await(result) shouldBe Some(Seq(new Enrolment("key", Seq(), "state")))
    }

    "return a valid sequence with multiple enrolments" in {
      val target = setupConnector(
        """[{"key":"key","identifiers":[],"state":"state"},{"key":"key2","identifiers":[{"key":"key","value":"value"}],"state":"state2"}]""", 200)
      val result = target.getEnrolmentsResponse("")

      await(result) shouldBe Some(Seq(new Enrolment("key", Seq(), "state"), new Enrolment("key2", Seq(new Identifier("key", "value")), "state2")))
    }
  }
}
