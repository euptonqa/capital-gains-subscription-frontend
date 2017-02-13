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
import forms.YesNoForm
import models.{CompanyAddressModel, YesNoModel}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressConfirmController @Inject()(appConfig: AppConfig,
                                                       val messagesApi: MessagesApi,
                                                       stateService: KeystoreConnector,
                                                       actions: AuthorisedActions)
  extends FrontendController {

  val correspondenceAddressConfirm: Action[AnyContent] =
    actions.authorisedNonResidentOrganisationAction { implicit user =>
      implicit request =>

        for {
          registrationDetails <- stateService.fetchAndGetBusinessData()
          existingAnswer <- stateService.fetchAndGetFormData[YesNoModel](KeystoreKeys.useRegistrationAddressKey)
        } yield {
          (existingAnswer, registrationDetails) match {

            case (_, None) =>
              Logger.warn("Failed to retrieved registration details from BusinessCustomer keystore")
              InternalServerError(views.html.error_template)

            case (None, Some(details)) =>
              val emptyForm = new YesNoForm(messagesApi).yesNoForm
              Ok(views.html.useRegisteredAddress(appConfig, emptyForm, details.businessAddress))

            case (Some(data), Some(details)) =>
              val populatedForm = new YesNoForm(messagesApi).yesNoForm.fill(data)
              Ok(views.html.useRegisteredAddress(appConfig, populatedForm, details.businessAddress))
          }
        }
    }

  val submitCorrespondenceAddressConfirm: Form[YesNoModel] => Action[AnyContent] =
    form => actions.authorisedNonResidentOrganisationAction { implicit user =>
      implicit request =>

        stateService.fetchAndGetBusinessData().flatMap {
          case None =>
            Logger.warn("Failed to retrieved registration details from BusinessCustomer keystore")
            Future.successful(InternalServerError(views.html.error_template))

          case Some(details) => processRequest(details.businessAddress)
        }

        def processRequest(address: CompanyAddressModel): Future[Result] = {
          form.fold(
            errors => Future.successful(BadRequest(views.html.useRegisteredAddress(appConfig, errors, address))),

            model => {
              for {
                _ <- stateService.saveFormData(KeystoreKeys.useRegistrationAddressKey, model)
                _ <- if (model.response) stateService.saveFormData(KeystoreKeys.correspondenceAddressKey, address) else Future()
              } yield {
                Ok(views.html.helloworld.hello_world(appConfig))
              }
            }
          )
        }
    }
}