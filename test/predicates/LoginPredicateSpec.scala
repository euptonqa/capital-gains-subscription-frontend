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

package predicates

import java.net.URI

import data.TestUserBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class LoginPredicateSpec extends UnitSpec with WithFakeApplication {

  val dummyUri = new URI("http://example.com")

  "Instantiating LoginPredicate" when {

    "supplied with an authContext with a weak credential should return true for page visibility" in {
      val predicate = new LoginPredicate(dummyUri)
      val authContext = TestUserBuilder.weakUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "supplied with an authContext with no credential should return false for page visibility" in {
      val predicate = new LoginPredicate(dummyUri)
      val authContext = TestUserBuilder.noCredUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }
}
