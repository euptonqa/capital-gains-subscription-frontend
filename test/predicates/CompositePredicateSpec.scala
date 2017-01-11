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
import com.google.inject.{Inject, Singleton}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import config.AppConfig
import play.api.inject.Injector

@Singleton
class CompositePredicateSpec extends UnitSpec with WithFakeApplication {

  "Calling the CompositePredicate when supplied with appropriate URIs" should {
    val injector: Injector = fakeApplication.injector
    def appConfig: AppConfig = injector.instanceOf[AppConfig]

    val postSignURI = "http://post-sigin-example.com"
    val notAuthorisedRedirectURI = "http://not-authorised-example.com"
    val ivUpliftURI = appConfig.ivUpliftUrl
    val twoFactorURI = appConfig.twoFactorUrl

    implicit val fakeRequest = FakeRequest()

    val predicate = new CompositePredicate(appConfig)(postSignURI,
      notAuthorisedRedirectURI,
      ivUpliftURI,
      twoFactorURI)

    "return true for page visibility when all supplied predicates are given an AuthContext that passes their associated checks" in {
      val authContext = TestUserBuilder.compositePredicateUserPass
      val result = predicate(authContext, fakeRequest)

      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe true
    }

    "return false for page visibility when all supplied predicates are given an AuthContext that passes their associated checks" in {
      val authContext = TestUserBuilder.compositePredicateUserFail
      val result = predicate(authContext, fakeRequest)

      val pageVisibility = await(result)

      pageVisibility.isVisible shouldBe false
    }
  }

}
