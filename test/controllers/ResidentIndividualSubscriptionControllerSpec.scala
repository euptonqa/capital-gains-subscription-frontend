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

package controllers

import org.scalatest.mock.MockitoSugar
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._
import services.AuthorisationService

class ResidentIndividualSubscriptionControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  def createMockService(): AuthorisationService = {
    val mockService = mock[AuthorisationService]

    mockService
  }

  "Calling .residentIndividualSubscription" when {

    "provided with a valid user" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val service = createMockService()

      lazy val target = new ResidentIndividualSubscriptionController(service)
      lazy val result = target.residentIndividualSubscription(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the hello world page" in {
        redirectLocation(result) shouldBe Some(routes.HelloWorld.helloWorld().url)
      }
    }

    "provided with an invalid user" should {

    }
  }
}
