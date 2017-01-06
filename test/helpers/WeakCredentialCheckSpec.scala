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
import builders.TestUserBuilder._

class WeakCredentialCheckSpec extends UnitSpec {
  "Calling .weakCredentialCheck" should {
    "return false when supplied with an AuthContext with CredentialStrength None" in {
      await(WeakCredentialCheck.weakCredentialCheck(noCredUserAuthContext)) shouldBe false
    }

    "return true when supplied with an AuthContext with CredentialStrength Weak" in {
      await(WeakCredentialCheck.weakCredentialCheck(weakUserAuthContext)) shouldBe true
    }

    "return true when supplied with an AuthContext with CredentialStrength Strong" in {
      await(WeakCredentialCheck.weakCredentialCheck(strongUserAuthContext)) shouldBe true
    }
  }
}
