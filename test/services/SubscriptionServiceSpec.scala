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

import connectors.SubscriptionConnector
import models.{FullDetails, SubscriptionReference}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class SubscriptionServiceSpec extends UnitSpec with MockitoSugar {

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  def mockedService(response: Option[SubscriptionReference]): SubscriptionService = {
    val mockConnector = mock[SubscriptionConnector]

    when(mockConnector.getSubscriptionResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    when(mockConnector.getSubscriptionResponseGhost(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new SubscriptionService(mockConnector)
  }

  "Calling SubscriptionService .getSubscription response" should {
    "return a SubscriptionReference model with a valid request" in {
      val service = mockedService(Some(SubscriptionReference("CGT-2121")))

      val result = service.getSubscriptionResponse("blah")

      await(result) shouldBe Some(SubscriptionReference("CGT-2121"))
    }
    "return None with an invalid request" in {
      val service = mockedService(None)
      val result = service.getSubscriptionResponse("blah")

      await(result) shouldBe None
    }
  }

  "Calling SubscriptionService .getGhostSubscription response" should {
    "return a SubscriptionReference model with a valid request" in {

      val fullDetailsModel = new FullDetails("john", "smith", "addressLineOne",
        "addressLineTwo", "town", "county", "postcode", "country")

      val service = mockedService(Some(SubscriptionReference("CGT-2123")))

      val result = service.getSubscriptionResponseGhost(fullDetailsModel)

      await(result) shouldBe Some(SubscriptionReference("CGT-2123"))
    }

    "return None when called with an invalid request" in {
      val invalidFullDetailsModel = new FullDetails("name of an invalid character length", "smith", "addressLineOne",
        "addressLineTwo", "town", "county", "postcode", "country")

      val service = mockedService(None)

      val result = service.getSubscriptionResponseGhost(invalidFullDetailsModel)

      await(result) shouldBe None
    }
  }
}
