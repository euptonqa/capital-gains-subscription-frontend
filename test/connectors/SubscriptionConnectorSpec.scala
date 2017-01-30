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
import models.{FullDetails, SubscriptionReference}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import config.{AppConfig, WSHttp}
import org.mockito.Mockito._
import play.api.http.Status._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class SubscriptionConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val nino = TestUserBuilder.createRandomNino
  implicit val hc = HeaderCarrier()

  def cgtSubscriptionResponse(cgtRef: String): JsValue = Json.toJson(SubscriptionReference(cgtRef))

  val config = mock[AppConfig]

  lazy val target = new SubscriptionConnector(mockHttp, config) {
    override lazy val serviceUrl: String = "test"
    override val subscriptionUrl: String  = "test"
  }

  lazy val mockHttp = mock[WSHttp]

  "SubscriptionConnector .getSubscriptionResponse with a valid request" should {

    val dummyRef = "CGT-2122"

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionResponse("fakeNino")(hc))
    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference ${dummyRef}" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionResponse with an invalid request" should {

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionResponse("fakeNino")(hc))

    "return None" in {
        result shouldBe None
    }
  }

  "SubscriptionConnecter .getSubscriptionResponseGhost with a valid request" should {
    val dummyRef = "CGT-2134"

    val model = FullDetails("john", "smith", "addressLineOne",
      "addressLineTwo", "town", "county", "postcode", "country")

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionResponseGhost((model)))

    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference ${dummyRef}" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionResponseGhost with an invalid request" should {
    val dummyRef = "CGT-2134"

    val model = FullDetails("name of an invalid character length", "smith", "addressLineOne",
      "addressLineTwo", "town", "county", "postcode", "country")

    when(mockHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionResponseGhost(model))

    "return None" in {
      result shouldBe None
    }
  }

}
