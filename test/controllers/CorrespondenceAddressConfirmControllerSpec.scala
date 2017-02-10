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

import java.util.UUID

import assets.{ControllerTestSpec, FakeRequestHelper, MessageLookup}
import config.{AppConfig, BusinessCustomerSessionCache, SubscriptionSessionCache}
import connectors.KeystoreConnector
import models.{CompanyAddressModel, ReviewDetails}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.inject.Injector

import scala.concurrent.Future

class CorrespondenceAddressConfirmControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper {

  val injector: Injector = fakeApplication.injector

  def appConfig: AppConfig = injector.instanceOf[AppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def mockKeystoreConnector(businessData: Option[ReviewDetails], addressData: Option[CompanyAddressModel]) = {
    lazy val sessionId = UUID.randomUUID.toString
    lazy val businessCustomerSessionCache = mock[BusinessCustomerSessionCache]
    lazy val subscriptionSessionCache = mock[SubscriptionSessionCache]
    lazy implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

    when(businessCustomerSessionCache.fetchAndGetEntry[ReviewDetails](ArgumentMatchers.eq("BC_Business_Details"))(ArgumentMatchers.any(),
      ArgumentMatchers.any())).thenReturn(Future.successful(businessData))

    when(subscriptionSessionCache.fetchAndGetEntry[CompanyAddressModel](ArgumentMatchers.anyString())
      (ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(addressData))

    new KeystoreConnector(appConfig, subscriptionSessionCache, businessCustomerSessionCache)
  }


  "Calling .correspondenceAddressConfirm" when {

    "supplied with valid data" should {
      lazy val testData = Some(ReviewDetails("string", Some(""), CompanyAddressModel(None, None, None, None, None, None),
        "sap", "safeId", false, false, Some("lastname"), Some("")))
      lazy val addressData = CompanyAddressModel(None, None, None, None, None, None)

      lazy val connector = mockKeystoreConnector(testData, Some(addressData))

      lazy val controller: CorrespondenceAddressFinalConfirmationController =
        new CorrespondenceAddressFinalConfirmationController(appConfig, messagesApi,
          connector)

      lazy val result = controller.correspondenceAddressFinalConfirmation(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the reviewBusinessDetails page" in {
        Jsoup.parse(bodyOf(result)).title() shouldBe MessageLookup.ReviewBusinessDetails.title
      }

    }

    "supplied with invalid data" should {
      lazy val testData = None
      lazy val addressData = CompanyAddressModel(None, None, None, None, None, None)

      lazy val connector = mockKeystoreConnector(testData, Some(addressData))

      lazy val controller: CorrespondenceAddressFinalConfirmationController =
        new CorrespondenceAddressFinalConfirmationController(appConfig, messagesApi,
          connector)

      lazy val result = controller.correspondenceAddressFinalConfirmation(fakeRequest)

      "return a status of 400" in {
        status(result) shouldBe 400
      }
    }
  }

  "Calling .submitCorrespondenceAddressConfirm" when {

  }

}
