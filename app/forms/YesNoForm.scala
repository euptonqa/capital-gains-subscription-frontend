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

package forms

import com.google.inject.Inject
import models.YesNoModel
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Request

class YesNoForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  private val nonEmptyCheck: String => Boolean = !_.isEmpty

  private val textToBoolean: String => Boolean = {
    case "Yes" => true
    case "No" => false
  }

  private val booleanToText: Boolean => String = {
    if (_) "Yes"
    else "No"
  }

  val yesNoForm = Form(
    mapping(
      "response" -> text
        .verifying(Messages("errors.mandatory"), nonEmptyCheck)
        .transform(textToBoolean, booleanToText)
    )(YesNoModel.apply)(YesNoModel.unapply)
  )

  def validate[R](hasErrors: Form[YesNoModel] => R, success: YesNoModel => R)(implicit request: Request[_]): R =
    yesNoForm.bindFromRequest.fold(hasErrors, success)
}
