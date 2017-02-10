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

import javax.inject.Inject

import config.AppConfig
import connectors.KeystoreConnector
import models.{CompanyAddressModel, CompanySubmissionModel, ReviewDetails}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.InternalServerException

import scala.concurrent.Future

class CorrespondenceAddressFinalConfirmationController @Inject()(appConfig: AppConfig, implicit val messagesApi: MessagesApi,
                                                                keystoreConnector: KeystoreConnector) extends FrontendController with I18nSupport {

  val correspondenceAddressFinalConfirmation = Action.async {
    implicit request =>

      val businessData = keystoreConnector.fetchAndGetBusinessData()
      val addressData: Future[Option[CompanyAddressModel]] = keystoreConnector.fetchAndGetFormData[CompanyAddressModel]("correspondenceAddress")
      def yieldBusinessData = {
        for{
          data <- businessData
          (companyAddress, name) <- (data.get.businessAddress, data.get.businessName)
          address <- addressData
        } yield {
          Future.successful(views.html.reviewBusinessDetails(appConfig, Some(companyAddress), address, name))
        }
      }

      //TODO: Obtain from keystore
      //TODO: Pass in businessFrontendDetails
      //TODO: Error handling

      yieldBusinessData.recoverWith{
        case error: Exception => Future.successful(BadRequest(error.getMessage))}
  }

  val submitCorrespondenceAddressFinalConfirmation = TODO

}
