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

import assets.{ControllerTestSpec, MessageLookup}
import forms.CorrespondenceAddressForm
import org.jsoup.Jsoup
import play.api.test.FakeRequest

class EnterCorrespondenceAddressControllerSpec extends ControllerTestSpec {

  "Calling .enterCorrespondenceAddress" when {

    //TODO: This is just in preparation for the update once the action has been created.
    "using correct authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, messagesApi)
      lazy val result = controller.enterCorrespondenceAddress(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "display the user details view" in {
        document.title() shouldBe MessageLookup.EnterCorrespondenceDetails.title
      }
    }
  }

  "Calling .submitCorrespondenceAddress" when {

  }

}
