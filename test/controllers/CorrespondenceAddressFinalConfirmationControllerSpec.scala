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

import assets.ControllerTestSpec
import connectors.KeystoreConnector
import models.{CompanyAddressModel, ReviewDetails, SubscriptionReference}
import org.mockito.ArgumentMatchers
import services.SubscriptionService
import org.mockito.Mockito._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CorrespondenceAddressFinalConfirmationControllerSpec extends ControllerTestSpec {

  "Calling .correspondenceAddressFinalConfirmation" when {

  }

  "Calling .submitCorrespondenceAddressFinalConfirmation" when {
    def createMockPostController(companyAddressModel: Option[CompanyAddressModel],
                                 referenceResponse: Future[Option[SubscriptionReference]],
                                 businessData: Option[ReviewDetails]) = {

      val mockService = mock[SubscriptionService]
      val mockKeystoreConnector = mock[KeystoreConnector]

      when(mockKeystoreConnector.fetchAndGetFormData[CompanyAddressModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(companyAddressModel))

      when(mockKeystoreConnector.fetchAndGetBusinessData()(ArgumentMatchers.any()))
        .thenReturn(Future.successful(businessData))

      when(mockService.getSubscriptionResponseCompany(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(referenceResponse)

      new CorrespondenceAddressFinalConfirmationController(mockService, mockKeystoreConnector)
    }

    val validBusinessData = ReviewDetails("", None, mock[CompanyAddressModel], "123456789", "123456789", false, false, None)

    "the cgt reference is retrieved correctly" should {
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(Some(SubscriptionReference("CGT123456"))),
        Some(validBusinessData))
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "have a status of 303" in {
        status(result) shouldBe 303
      }

      "load the cgt confirmation page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.CGTSubscriptionController.confirmationOfSubscription("CGT123456").url)
      }
    }

    "there is no keystore data available" should {
      lazy val controller = createMockPostController(None, Future.successful(Some(SubscriptionReference("CGT123456"))), None)
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "have a status of 400" in {
        status(result) shouldBe 400
      }
    }

    "no business data is returned" should {
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(Some(SubscriptionReference("CGT123456"))), None)
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "have a status of 500" in {
        status(result) shouldBe 500
      }
    }

    "no CGT reference is returned" should {
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.successful(None), Some(validBusinessData))
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "have a status of 500" in {
        status(result) shouldBe 500
      }
    }

    "an error occurs during subscription" should {
      lazy val exception = new Exception("testMessage")
      lazy val controller = createMockPostController(Some(CompanyAddressModel(None, None, None, None, None, None)),
        Future.failed(exception),
        Some(validBusinessData))
      lazy val result = controller.submitCorrespondenceAddressFinalConfirmation(FakeRequest("POST", ""))

      "have a status of 500" in {
        status(result) shouldBe 500
      }
    }
  }
}
