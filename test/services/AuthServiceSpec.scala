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

import builders.TestUserBuilder
import common.Constants
import models.AuthDataModel
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.frontend.auth.connectors.domain.ConfidenceLevel
import uk.gov.hmrc.play.test.UnitSpec

class AuthServiceSpec extends UnitSpec {

  def affinityResponse(key:String, nino: String): JsValue = Json.parse(
    s"""{
        "uri":"/auth/oid/57e915480f00000f006d915b",
        "confidenceLevel":"200",
        "credentialStrength":"Strong",
        "userDetailsLink":"http://localhost:9978/user-details/id/57e915482200005f00b0b55e",
        "legacyOid":"57e915480f00000f006d915b",
        "new-session":"/auth/oid/57e915480f00000f006d915b/session",
        "ids":"/auth/oid/57e915480f00000f006d915b/ids",
        "credentials":
          {"gatewayId":"872334723473244"},
        "accounts":
          {"paye":
            {"nino": "$nino"}
          },
        "lastUpdated":"2016-09-26T12:32:08.734Z",
        "loggedInAt":"2016-09-26T12:32:08.734Z",
        "levelOfAssurance":"1",
        "enrolments":"/auth/oid/57e915480f00000f006d915b/enrolments",
        "affinityGroup":"$key",
        "correlationId":"9da194b9490024bae213f18d5b34fedf41f2c3236b434975333a7bdb0fe548ec",
        "credId":"872334723473244"
        }"""
  )

  "Calling AuthServiceSpec .getAuthData" should {

    val nino = TestUserBuilder.createRandomNino
    val result = AuthService.getAuthDataModel(affinityResponse("Individual", nino))

    "return an AuthDataModel" in {
      result shouldBe a[AuthDataModel]
    }

    "return an AuthDataModel containing a confidence level of 200" in {
      result.confidenceLevel shouldBe ConfidenceLevel.L200
    }

    "return an AuthDataModel containing a credential strength of Strong" in {
      result.credStrength shouldBe "Strong"
    }

    s"return an AuthDataModel containing a nino of $nino" in {
      result.nino shouldBe nino
    }

    "return an AuthDataModel containing a uri of /auth/oid/57e915480f00000f006d915b" in {
      result.uri shouldBe "/auth/oid/57e915480f00000f006d915b"
    }

    "return an AuthDataModel containing an Affinity Group of Individual" in {
      result.affinityGroup shouldBe Constants.AffinityGroup.Individual
    }
  }
}
