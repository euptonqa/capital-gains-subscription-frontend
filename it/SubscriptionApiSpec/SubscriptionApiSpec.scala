/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package SubscriptionApiSpec

import com.github.tomakehurst.wiremock.client.WireMock._
import config.{AppConfig, WSHttp}
import connectors.SubscriptionConnector
import itutil.{IntegrationSpecBase, WiremockHelper}
import models.SubscriptionReference
import play.api.libs.json.Json
import play.api.test.FakeApplication
import uk.gov.hmrc.play.http.HeaderCarrier

class SubscriptionApiSpec extends IntegrationSpecBase {

  implicit val hc = HeaderCarrier()

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: Int = WiremockHelper.wiremockPort
  val mockUrl = s"http://$mockHost:$mockPort/capital-gains-subscription"

  override implicit lazy val app = FakeApplication(additionalConfiguration = Map(
    "subscription.url" -> s"$mockUrl"
  ))

  "Calling the subscription subscribe/resident/individual" should {

    lazy val nino = "AA100100A"

    lazy val subscribeIndividualUrl = s"/capital-gains-subscription/subscribe/resident/individual/"
    lazy val subscriptionReferenceModel = SubscriptionReference("dummyReference")

    lazy val subscriptionConnector = new SubscriptionConnector(app.injector.instanceOf[WSHttp], app.injector.instanceOf[AppConfig])

    def subscriptionResponse = subscriptionConnector.getSubscriptionResponse(nino)

    "return the dummyReference" in {
      stubFor(post(urlPathEqualTo(subscribeIndividualUrl))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(Json.toJson(subscriptionReferenceModel).toString())
        )
      )

      await(subscriptionResponse) shouldBe Some(SubscriptionReference("dummyReference"))
    }
  }
}

