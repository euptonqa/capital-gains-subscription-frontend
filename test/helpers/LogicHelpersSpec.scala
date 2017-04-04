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

package helpers

import connectors.KeystoreConnector
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.Mockito._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class LogicHelpersSpec extends UnitSpec with OneAppPerSuite with MockitoSugar {

  def setupHelper(saveResult: Future[CacheMap]): LogicHelpers = {
    val mockKeystoreConnector = mock[KeystoreConnector]

    when(mockKeystoreConnector.saveFormData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(saveResult)

    new LogicHelpers(mockKeystoreConnector)
  }

  implicit val mockHeaderCarrier = mock[HeaderCarrier]

  "Calling .bindAndValidateCallbackUrl" when {

    "an error occurs during keystore saving the data" should {
      lazy val helper = setupHelper(Future.failed(new Exception("error message")))
      lazy val result = helper.bindAndValidateCallbackUrl("/test/route")

      "return an exception" in {
        lazy val exception = intercept[Exception]{await(result)}

        exception.getMessage shouldBe "error message"
      }
    }

    "a valid url is bound and saved" should {
      lazy val helper = setupHelper(Future.successful(mock[CacheMap]))
      lazy val result = helper.bindAndValidateCallbackUrl("/test/route")

      "return a true" in {
        await(result) shouldBe true
      }
    }
  }
}
