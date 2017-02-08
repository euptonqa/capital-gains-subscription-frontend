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
import assets.{ControllerTestSpec, MessageLookup}
import config.{AppConfig, SubscriptionSessionCache, WSHttp}
import connectors.KeystoreConnector
import forms.CorrespondenceAddressForm
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.config.ServicesConfig

class EnterCorrespondenceAddressControllerSpec extends ControllerTestSpec {

//  def createMockKeystoreConnector: KeystoreConnector = {
//    lazy val config: AppConfig = mock[AppConfig]
//    lazy val servicesConfig: ServicesConfig = mock[ServicesConfig]
//    lazy val subscriptionSessionCache: SubscriptionSessionCache = mock[SubscriptionSessionCache]
//
//    new KeystoreConnector(config, subscriptionSessionCache, servicesConfig)
//  }

  "Calling .enterCorrespondenceAddress" when {

    //TODO: This is just in preparation for the update once the action has been created.
    "using correct authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
//      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, /*keystoreConnector,*/ messagesApi)
      lazy val result = controller.enterCorrespondenceAddress(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "display the user details view" in {
        document.title() shouldBe MessageLookup.EnterCorrespondenceAddress.title
      }
    }
  }

  "Calling .submitCorrespondenceAddress" when {

    "with an invalid form" should {

      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody()
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
//      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, /*keystoreConnector,*/ messagesApi)
      lazy val result = controller.submitCorrespondenceAddress(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "return to the user details page" in {
        document.title() shouldBe MessageLookup.EnterCorrespondenceAddress.title
      }
    }
    "with a valid form" should {

      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
          "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
//      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, /*keystoreConnector,*/ messagesApi)
      lazy val result = controller.submitCorrespondenceAddress(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the confirm correspondence address details page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/company/correspondence-address-confirm")
      }
    }
  }
}
