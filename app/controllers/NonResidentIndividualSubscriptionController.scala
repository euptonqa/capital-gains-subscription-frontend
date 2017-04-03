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

import auth.{AuthorisedActions, CgtIndividual}
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import helpers.{EnrolmentToCGTCheck, LogicHelpers}
import common.Constants.ErrorMessages._
import common.Keys.KeystoreKeys
import connectors.KeystoreConnector
import models.CallbackUrlModel
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import services.{AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class NonResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                            appConfig: AppConfig,
                                                            subscriptionService: SubscriptionService,
                                                            authorisationService: AuthorisationService,
                                                            logicHelpers: LogicHelpers,
                                                            val messagesApi: MessagesApi)
  extends FrontendController with I18nSupport {

  val nonResidentIndividualSubscription: String => Action[AnyContent] = url => actions.authorisedNonResidentIndividualAction {
    implicit user =>
      implicit request =>

        val isValidRequest = logicHelpers.bindAndValidateCallbackUrl(url)

        def routeRequest(alreadyEnrolled: Boolean, isValid: Boolean)(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {
          if (!isValid) Future.successful(BadRequest(views.html.error_template(Messages("errors.badRequest"),
            Messages("errors.badRequest"), Messages("errors.checkAddress"), appConfig)))
          else if (alreadyEnrolled) Future.successful(Redirect(url))
          else notEnrolled()
        }

        for {
          isValid <- isValidRequest
          enrolments <- authorisationService.getEnrolments(hc(request))
          checkEnrolled <- EnrolmentToCGTCheck.checkIndividualEnrolments(enrolments)
          route <- routeRequest(checkEnrolled, isValid)
        } yield route
  }

  def notEnrolled()(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {
    if (user.nino.isDefined) subscribeAndEnrolWithNino(user.nino.get)
    else Future.successful(Redirect(routes.UserDetailsController.userDetails()))
  }

  def subscribeAndEnrolWithNino(nino: String)(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {

    subscriptionService.getSubscriptionNonResidentNinoResponse(nino)(hc).map {
      case Some(result) => Redirect(routes.CGTSubscriptionController.confirmationOfSubscription(result.cgtRef))
      case None => throw new Exception(failedToEnrolIndividual)
    }
  }
}
