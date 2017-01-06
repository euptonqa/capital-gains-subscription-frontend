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

import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.{HttpGet, HttpPost}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class AuthConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication{

  object TestAuthConnector extends AuthConnector {
    override def serviceUrl: String = "localhost"
    override def authorityUri: String = "auth/authority"
    override def http: HttpGet with HttpPost = mock[HttpGet with HttpPost]
  }

  "AuthConnector .getAuthResponse" should {


    val result = TestAuthConnector.getAuthResponse()

    "should have a status of 200" in {
      status(result) shouldBe 200
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
    }
  }



















  "AuthService .getConfidenceLevel" should {

  }

  "AuthService. getCredentialStrength" should {

  }
}
