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

import models.{Enrolment, Identifier}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsString, Json, OFormat}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class GovernmentGatewayConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  implicit val hc = mock[HeaderCarrier]

  def setupConnector(jsonString: String, status: Int): GovernmentGatewayConnector = {

    val mockHttp = mock[HttpGet with HttpPost]

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(Json.parse(jsonString)))))

    new GovernmentGatewayConnector {
      override val http = mockHttp
    }
  }

  "Calling .getEnrolments" should {

    "return a None with a failed response" in {
      val connector = setupConnector("[]", 500)
      val result = connector.getEnrolments("")

      await(result) shouldBe None
    }

    "return an empty sequence with an empty json response" in {
      val connector = setupConnector("[]", 200)
      val result = connector.getEnrolments("")

      await(result) shouldBe Some(Seq())
    }

    "return a valid sequence with an single enrolment" in {
      val connector = setupConnector("""[{"key":"key","identifiers":[],"state":"state"}]""", 200)
      val result = connector.getEnrolments("")

      await(result) shouldBe Some(Seq(new Enrolment("key", Seq(), "state")))
    }

    "return a valid sequence with multiple enrolments" in {
      val connector = setupConnector(
        """[{"key":"key","identifiers":[],"state":"state"},{"key":"key2","identifiers":[{"key":"key","value":"value"}],"state":"state2"}]""", 200)
      val result = connector.getEnrolments("")

      await(result) shouldBe Some(Seq(new Enrolment("key", Seq(), "state"), new Enrolment("key2", Seq(new Identifier("key", "value")), "state2")))
    }
  }
}
