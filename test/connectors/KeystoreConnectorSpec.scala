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

import java.util.UUID

import config.{AppConfig, SubscriptionSessionCache, WSHttp}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.Future

class KeystoreConnectorSpec extends UnitSpec with MockitoSugar {

  lazy val mockSessionCache = mock[SessionCache]
  lazy val sessionId = UUID.randomUUID.toString
  lazy val http = mock[WSHttp]
  lazy val config = mock[AppConfig]
  lazy val servicesConfig = mock[ServicesConfig]
  lazy val subscriptionSessionCache = mock[SubscriptionSessionCache]

  lazy implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  lazy val target = new KeystoreConnector(config, subscriptionSessionCache, servicesConfig)

  "KeystoreConnector .fetchFormData" should {

    when(mockSessionCache.fetchAndGetEntry[String](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(Option("hello")))

    "should be able to retrieve a String" in {
      lazy val result = target.fetchAndGetFormData[String]("String")
      await(result) shouldBe "hello"
    }

  }

}
