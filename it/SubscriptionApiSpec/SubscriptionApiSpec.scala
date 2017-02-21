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

import assets.MessageLookup.{ContactDetails, UseRegisteredAddress}
import com.github.tomakehurst.wiremock.client.WireMock._
import itutil.{IntegrationSpecBase, WiremockHelper}
import models.{CompanyAddressModel, CompanySubmissionModel, ContactDetailsModel}
import play.api.libs.json.Json
import org.scalatest.mock.MockitoSugar

import play.api.test.FakeApplication
import uk.gov.hmrc.play.http.HeaderCarrier

class SubscriptionApiSpec extends IntegrationSpecBase {

  implicit val hc = HeaderCarrier()

  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort
  val mockUrl = s"http://$mockHost:$mockPort"

  override implicit lazy val app = FakeApplication(additionalConfiguration = Map(
    "subscription.url" -> mockUrl
  ))

  "Calling subscribe/company" should {

    val subscribeCompanyUrl = "capital-gains-subscription/subscribe/company"

    "return a 200 response" in {

      stubFor(post(urlMatching(subscribeCompanyUrl))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody("body")
        )
      )

      val response = buildClient("/company").get.futureValue
      response.status shouldBe 303
    }
  }

  "Calling subscribe/resident/individual" should {

    val subscribeIndividualUrl = "capital-gains-subscription/subscribe/individual"
    val cgtRef = "TestRef"

    "return a 303 response" in {

      stubFor(post(urlMatching(subscribeIndividualUrl))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(cgtRef)
        )
      )

      val response = buildClient("/resident/individual").get.futureValue
      response.status shouldBe 303
    }
  }

  "Calling /non-resident/individual" should {

    val subscribeNonIndividualUrl = "/capital-gains-subscription/subscribe/non-individual"

    "return a 303 response" in {
      stubFor(post(urlMatching(subscribeNonIndividualUrl))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody("body")
        )
      )

      val response = buildClient("/non-resident/individual").get.futureValue
      response.status shouldBe 303
      response.body shouldBe "body"
    }
  }
}
