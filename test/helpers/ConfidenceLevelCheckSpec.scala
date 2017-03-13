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

import uk.gov.hmrc.play.test.UnitSpec
import data.TestUserBuilder._

class ConfidenceLevelCheckSpec extends UnitSpec {
  "Calling .confidenceLevelCheck" should {
    "return true when supplied with an AuthContext with a ConfidenceLevel of 200" in {
      await(ConfidenceLevelCheck.confidenceLevelCheck(create200ConfidenceUserAuthContext)) shouldBe true
    }

    "return true when supplied with an AuthContext with a ConfidenceLevel of 300" in {
      await(ConfidenceLevelCheck.confidenceLevelCheck(create300ConfidenceUserAuthContext)) shouldBe true
    }

    "return true when supplied with an AuthContext with a ConfidenceLevel of 500" in {
      await(ConfidenceLevelCheck.confidenceLevelCheck(create500ConfidenceUserAuthContext)) shouldBe true
    }

    "return false when supplied with an AuthContext with a ConfidenceLevel of 200 or below (in this case 50)" in {
      await(ConfidenceLevelCheck.confidenceLevelCheck(noCredUserAuthContext)) shouldBe false
    }
  }
}
