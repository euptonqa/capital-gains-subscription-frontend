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
import common.Keys.KeystoreKeys
import config.AppConfig
import connectors.KeystoreConnector
import forms.ContactDetailsForm
import models.ContactDetailsModel
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ContactDetailsController @Inject()(appConfig: AppConfig,
                                         contactDetailsForm: ContactDetailsForm,
                                         val messagesApi: MessagesApi,
                                         keystoreConnector: KeystoreConnector,
                                         actions: AuthorisedActions) extends FrontendController with I18nSupport {

  val contactDetails: Action[AnyContent] = actions.authorisedNonResidentOrganisationAction() { implicit user =>
    implicit request =>
      keystoreConnector.fetchAndGetFormData[ContactDetailsModel](KeystoreKeys.contactDetailsKey).map {
        case Some(data) => Ok(views.html.contactDetails(appConfig, contactDetailsForm.contactDetailsForm.fill(data)))
        case _ => Ok(views.html.contactDetails(appConfig, contactDetailsForm.contactDetailsForm))
      }
  }

  val submitContactDetails: Action[AnyContent] = actions.authorisedNonResidentOrganisationAction() { implicit user =>
    implicit request =>

      val errorAction: Form[ContactDetailsModel] => Future[Result] = form => {
        Future.successful(BadRequest(views.html.contactDetails(appConfig, form)))
      }

      val successAction: ContactDetailsModel => Future[Result] = model => {
        keystoreConnector.saveFormData[ContactDetailsModel](KeystoreKeys.contactDetailsKey, model)
        Future.successful(Redirect(controllers.routes.CorrespondenceAddressFinalConfirmationController.correspondenceAddressFinalConfirmation()))
      }

      contactDetailsForm.contactDetailsForm.bindFromRequest.fold(errorAction, successAction)
  }
}
