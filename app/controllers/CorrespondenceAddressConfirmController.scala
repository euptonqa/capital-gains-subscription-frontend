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
import config.AppConfig
import forms.YesNoForm
import models.CompanyAddressModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressConfirmController @Inject()(appConfig: AppConfig,
                                                       yesNoForm: YesNoForm,
                                                       val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val correspondenceAddressConfirm = Action.async {
    implicit request => {
      val addressModel = CompanyAddressModel(
        Some("line1"),
        Some("line2"),
        Some("line3"),
        Some("line4"),
        Some("postCode"),
        Some("country")
      )
      Future.successful(Ok(views.html.useRegisteredAddress(appConfig, yesNoForm.yesNoForm, addressModel)))
    }
  }

  val submitCorrespondenceAddressConfirm = TODO

}
