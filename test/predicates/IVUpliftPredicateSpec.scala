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

import builders.TestUserBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec

class IVUpliftPredicateSpec extends UnitSpec {

  val dummyURI = new URI("http://example.com")

  "Calling the IVUpliftPredicate" should {

    "return a false for page visibility with CL100" in {
      val predicate =  new IVUpliftPredicate(dummyURI)
      val authContext = TestUserBuilder.create100ConfidenceUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }

    "return a true for page visibility with CL200" in {
      val predicate =  new IVUpliftPredicate(dummyURI)
      val authContext = TestUserBuilder.create200ConfidenceUserAuthContext

      val result = predicate(authContext, FakeRequest())
      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }
  }
}
