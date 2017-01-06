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

package builders

import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, Authority, ConfidenceLevel, CredentialStrength, PayeAccount}

import scala.util.Random

object TestUserBuilder {

  def createRandomNino: String = new Generator(new Random()).nextNino.nino.replaceFirst("MA", "AA")

  val testNINO = createRandomNino

  val weakUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.Weak, ConfidenceLevel.L50, None, Some("testEnrolmentUri"), None, ""))
  }

  val strongUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.Strong, ConfidenceLevel.L50, None, Some("testEnrolmentUri"), None, ""))
  }

  val create200ConfidenceUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.Strong,
      ConfidenceLevel.L200, None, Some("testEnrolmentUri"), None, ""))
  }

  val create300ConfidenceUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.Strong,
      ConfidenceLevel.L300, None, Some("testEnrolmentUri"), None, ""))
  }

  val create500ConfidenceUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.Strong,
      ConfidenceLevel.L500, None, Some("testEnrolmentUri"), None, ""))
  }
  val noCredUserAuthContext: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(), None, None, CredentialStrength.None, ConfidenceLevel.L50, None, Some("testEnrolmentUri"), None, ""))
  }

  val userWithNINO: AuthContext = {
    AuthContext.apply(Authority("testUserId", Accounts(paye = Some(PayeAccount(s"/paye/$testNINO", Nino(testNINO)))), None, None,
      CredentialStrength.None, ConfidenceLevel.L50, None, None, None, ""))
  }
}
