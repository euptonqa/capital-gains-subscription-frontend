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

import auth.AuthorisedActions
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import forms.UserFactsForm
import models.UserFactsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import services.SubscriptionService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class UserDetailsController @Inject()(appConfig: AppConfig, fullDetailsForm: UserFactsForm,
                                      val messagesApi: MessagesApi, actions: AuthorisedActions,
                                      subscriptionService: SubscriptionService)
  extends FrontendController with I18nSupport {

  def subscribeUser(userFactsModel: UserFactsModel)(implicit hc: HeaderCarrier): Future[Try[String]] = {
    subscriptionService.getSubscriptionResponseGhost(userFactsModel).map[Try[String]] {
      case Some(data) => Success(data.cgtRef)
      case _ => Failure(new Exception("No data found"))
    }
  }

  val userDetails: Action[AnyContent] = actions.authorisedNonResidentIndividualAction { implicit user => implicit request =>
    Future.successful(Ok(views.html.userDetails(appConfig, fullDetailsForm.fullDetailsForm)))
  }

  val submitUserDetails: Action[AnyContent] = actions.authorisedNonResidentIndividualAction { implicit user => implicit request =>

    val successAction: UserFactsModel => Future[Result] = model => {

      def action(cgtRef: Try[String]) = {
        cgtRef match {
          case Success(ref) => Future.successful(Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(ref)))
          case Failure(error) => Future.successful(InternalServerError(Json.toJson("Bad cgt ref" + error.getMessage)))
        }
      }

      for {
        ref <- subscribeUser(model)
        action <- action(ref)
      } yield action
    }

    fullDetailsForm.fullDetailsForm.bindFromRequest.fold(errors => Future.successful(BadRequest(views.html.userDetails(appConfig, errors))),
      successAction)
  }
}
