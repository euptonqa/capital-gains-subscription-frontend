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
import models.{CompanyAddressModel, CompanySubmissionModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

class CorrespondenceAddressFinalConfirmationController @Inject()(appConfig: AppConfig, implicit val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val correspondenceAddressFinalConfirmation = Action.async {
    implicit request =>
      //TODO: Obtain from keystore
      //TODO: Pass in businessFrontendDetails
      //TODO: Error handling
      val registeredModel = Some(CompanyAddressModel(Some("hello"), Some("hello"), None, None, None, None))
      val contactModel = Some(CompanyAddressModel(Some("hello"), Some("hello"), None, None, None, None))
      Future.successful(Ok(views.html.reviewBusinessDetails(appConfig, registeredModel, contactModel)))
  }

  val submitCorrespondenceAddressFinalConfirmation = TODO

}
