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
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import org.mockito.Mockito._
import play.api.http.Status._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import scala.concurrent.Future

class SubscriptionConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val nino = TestUserBuilder.createRandomNino
  implicit val hc = HeaderCarrier()

  def cgtSubscriptionResponse(cgtRef: String): JsValue = Json.parse(
    s"""{"cgtRef":"$cgtRef"}""".stripMargin
  )

  lazy val target = new SubscriptionConnector(mockHttp) {
    override lazy val serviceUrl: String = "test"
    override val subscriptionUrl: String  = "test"
  }

  lazy val mockHttp = mock[WSHttp]

  "SubscriptionConnector .getSubscriptionResponse with a valid request" should {
    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse("CGT-2122")))))

    val result = await(target.getSubscriptionResponse("fakeNino")(hc)).get
    "return a valid String" in {
      result shouldBe a[String]
    }

    "return a"
  }

}
