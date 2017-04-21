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

import auth.AuthorisedActions
import common.Keys.{KeystoreKeys => keys}
import config.AppConfig
import connectors.KeystoreConnector
import models.RedirectModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CGTSubscriptionController @Inject()(keystoreConnector: KeystoreConnector,
                                          authorisedActions: AuthorisedActions,
                                          appConfig: AppConfig,
                                          val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val confirmationOfSubscriptionResidentIndv: String => Action[AnyContent] = cgtReference => authorisedActions.authorisedResidentIndividualAction() {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.confirmation.cgtSubscriptionConfirmation(appConfig, cgtReference,
          routes.CGTSubscriptionController.submitConfirmationOfSubscriptionResidentIndv())))
  }

  val confirmationOfSubscriptionNonResIndv: String => Action[AnyContent] = cgtReference => authorisedActions.authorisedNonResidentIndividualAction() {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.confirmation.cgtSubscriptionConfirmation(appConfig, cgtReference,
          routes.CGTSubscriptionController.submitConfirmationOfSubscriptionNonResIndv())))
  }

  val confirmationOfSubscriptionCompany: String => Action[AnyContent] = cgtReference => authorisedActions.authorisedNonResidentOrganisationAction() {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.confirmation.cgtSubscriptionConfirmation(appConfig, cgtReference,
          routes.CGTSubscriptionController.submitConfirmationOfSubscriptionCompany())))
  }

  val submitConfirmationOfSubscriptionResidentIndv: Action[AnyContent] = authorisedActions.authorisedResidentIndividualAction() {
    implicit user =>
      implicit request =>
        keystoreConnector.fetchAndGetFormData[RedirectModel](keys.redirect) flatMap {
          case Some(model) => Future.successful(Redirect(model.url))
          case _ => throw new Exception("Failed to find a callback URL")
    }
  }

  val submitConfirmationOfSubscriptionNonResIndv: Action[AnyContent] = authorisedActions.authorisedNonResidentIndividualAction() {
    implicit user =>
      implicit request =>
        keystoreConnector.fetchAndGetFormData[RedirectModel](keys.redirect) flatMap {
          case Some(model) => Future.successful(Redirect(model.url))
          case _ => throw new Exception("Failed to find a callback URL")
    }
  }

  val submitConfirmationOfSubscriptionCompany: Action[AnyContent] = authorisedActions.authorisedNonResidentOrganisationAction() {
    implicit user =>
      implicit request =>
        keystoreConnector.fetchAndGetFormData[RedirectModel](keys.redirect) flatMap {
          case Some(model) => Future.successful(Redirect(model.url))
          case _ => throw new Exception("Failed to find a callback URL")
    }
  }
}
