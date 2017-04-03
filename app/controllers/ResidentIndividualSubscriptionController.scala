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

import javax.inject.{Inject, Singleton}

import auth.{AuthorisedActions, CgtIndividual}
import common.Keys.KeystoreKeys
import config.AppConfig
import connectors.KeystoreConnector
import helpers.{EnrolmentToCGTCheck, LogicHelpers}
import models.CallbackUrlModel
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                         appConfig: AppConfig,
                                                         subscriptionService: SubscriptionService,
                                                         authService: AuthorisationService,
                                                         logicHelpers: LogicHelpers,
                                                         val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val residentIndividualSubscription: String => Action[AnyContent] = url =>
    actions.authorisedResidentIndividualAction {
      implicit user =>
        implicit request =>

          val isValidRequest = logicHelpers.bindAndValidateCallbackUrl(url)

          for {
            validate <- isValidRequest
            enrolments <- authService.getEnrolments
            isEnrolled <- EnrolmentToCGTCheck.checkIndividualEnrolments(enrolments)
            redirect <- checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user, isEnrolled, validate, url)
          } yield redirect
    }

  def checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user: CgtIndividual,
                                                                  isEnrolled: Boolean,
                                                                  isValid: Boolean,
                                                                  url: String)(implicit hc: HeaderCarrier, request: Request[AnyContent]): Future[Result] = {
    if (!isValid) Future.successful(BadRequest(views.html.error_template(Messages("errors.badRequest"),
      Messages("errors.badRequest"), Messages("errors.checkAddress"), appConfig)))
    else if (isEnrolled) Future.successful(Redirect(url))
    //TODO: you're already enrolled to CGT!
    else  checkForCgtRefAndRedirectToConfirmation(user)
  }

  def checkForCgtRefAndRedirectToConfirmation(user: CgtIndividual)(implicit hc: HeaderCarrier): Future[Result] = {

    subscriptionService.getSubscriptionResponse(user.nino.get).map {
      case Some(response) => Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(response.cgtRef))
      case _ => Redirect("http://www.gov.uk")
    }
  }
}
