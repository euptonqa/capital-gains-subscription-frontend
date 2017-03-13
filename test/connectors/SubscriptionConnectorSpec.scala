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

import models.{AgentSubmissionModel, CompanySubmissionModel, SubscriptionReference, UserFactsModel}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import config.{AppConfig, WSHttp}
import data.TestUserBuilder
import org.mockito.Mockito._
import play.api.http.Status._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class SubscriptionConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val nino: String = TestUserBuilder.createRandomNino
  implicit val hc = HeaderCarrier()

  def cgtSubscriptionResponse(cgtRef: String): JsValue = Json.toJson(SubscriptionReference(cgtRef))

  val config: AppConfig = mock[AppConfig]

  lazy val target = new SubscriptionConnector(mockHttp, config) {
    override lazy val serviceUrl: String = "test"
    override val subscriptionResidentUrl: String  = "test"
    override val subscriptionNonResidentUrl: String  = "testNR"
    override val subscriptionNonResidentNinoUrl: String  = "testNRWithNino"
  }

  lazy val mockHttp: WSHttp = mock[WSHttp]

  "SubscriptionConnector .getSubscriptionResponse with a valid request" should {

    val dummyRef = "CGT-2122"

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionResponse("fakeNino")(hc))
    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference $dummyRef" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionResponse with an invalid request" should {

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionResponse("fakeNino")(hc))

    "return None" in {
        result shouldBe None
    }
  }

  "SubscriptionConnector .getSubscriptionNonResidentNinoResponse with a valid request" should {

    val dummyRef = "CGT-2122"

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionNonResidentNinoResponse("fakeNino")(hc))
    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference $dummyRef" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionNonResidentNinoResponse with an invalid request" should {

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionNonResidentNinoResponse("fakeNino")(hc))

    "return None" in {
      result shouldBe None
    }
  }

  "SubscriptionConnector .getSubscriptionResponseGhost with a valid request" should {
    val dummyRef = "CGT-2134"

    val model = UserFactsModel("john", "smith", "addressLineOne",
      Some("addressLineTwo"), "town", Some("county"), "postcode", "country")

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionResponseGhost(model))

    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference $dummyRef" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionResponseGhost with an invalid request" should {

    val model = UserFactsModel("name of an invalid character length", "smith", "addressLineOne",
      Some("addressLineTwo"), "town", Some("county"), "postcode", "country")

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionResponseGhost(model))

    "return None" in {
      result shouldBe None
    }
  }

  "SubscriptionConnecter .getSubscriptionResponseCompany with a valid request" should {
    val dummyRef = "CGT-2134"

    val model = CompanySubmissionModel(Some("123456789"), None, None, None)

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(OK, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.getSubscriptionResponseCompany(model))

    "return a valid SubscriptionReference" in {
      result.get shouldBe a[SubscriptionReference]
    }

    s"return a SubscriptionReference containing the reference $dummyRef" in {
      result.get.cgtRef shouldBe dummyRef
    }
  }

  "SubscriptionConnector .getSubscriptionResponseCompany with an invalid request" should {

    val model = CompanySubmissionModel(Some("123456789"), None, None, None)

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.getSubscriptionResponseCompany(model))

    "return None" in {
      result shouldBe None
    }
  }

  "SubscriptionConnector .enrolAgent() with a valid request" should {
    val dummyRef = "CGT-2134"

    val model = AgentSubmissionModel("123456789", "ARN123456")

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(NO_CONTENT, Some(cgtSubscriptionResponse(dummyRef)))))

    val result = await(target.enrolAgent(model))

    "return a valid SuccessAgentEnrolmentResponse" in {
      result shouldBe SuccessAgentEnrolmentResponse
    }
  }

  "SubscriptionConnector .enrolAgent() with an invalid request" should {

    val model = AgentSubmissionModel("123456789", "ARN123456")

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).
      thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(Json.toJson("invalid:n")))))

    val result = await(target.enrolAgent(model))

    "return a valid FailedAgentEnrolmentResponse" in {
      result shouldBe FailedAgentEnrolmentResponse
    }
  }
}
