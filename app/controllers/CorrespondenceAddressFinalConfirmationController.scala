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

import com.google.inject.{Inject, Singleton}
import connectors.KeystoreConnector
import models.CompanyAddressModel
import play.api.mvc.{Action, Result}
import services.SubscriptionService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressFinalConfirmationController @Inject()(subscriptionService: SubscriptionService,
                                                                 keystoreConnector: KeystoreConnector) extends FrontendController {

  val correspondenceAddressFinalConfirmation = TODO

  val submitCorrespondenceAddressFinalConfirmation = Action.async { implicit request =>

    def successAction(companyAddressModel: CompanyAddressModel): Future[Result] = {
      //TODO replace stub with actual call when created
      val result = Future.successful("CGT123456")

      result.map { reference =>
        Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(reference))
      }.recoverWith {
        case error => Future.successful(InternalServerError(error.getMessage))
      }
    }

    //TODO replace mock key with actual key
    val companyAddress = keystoreConnector.fetchAndGetFormData[CompanyAddressModel]("mockKey")

    companyAddress.flatMap {
      case Some(data) => successAction(data)
      case None => Future.successful(BadRequest(""))
    }
  }
}
