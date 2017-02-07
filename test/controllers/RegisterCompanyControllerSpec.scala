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

import akka.util.Timeout
import assets.ControllerTestSpec
import play.api.test.FakeRequest
import play.api.test.Helpers.redirectLocation

class RegisterCompanyControllerSpec extends ControllerTestSpec {

  implicit val timeout: Timeout = mock[Timeout]

  "Calling .registerCompany" when {

    lazy val fakeRequest = FakeRequest("GET", "/")
    lazy val registerCompanyController: RegisterCompanyController = new RegisterCompanyController(mockConfig)
    lazy val result = await(registerCompanyController.registerCompany(fakeRequest))

    "there is nothing special going on to begin with" should {

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the business customer frontend" in {
        redirectLocation(result).get.toString shouldBe "http://localhost:9923/business-customer/business-verification/capital-gains-tax"
      }
    }
  }
}
