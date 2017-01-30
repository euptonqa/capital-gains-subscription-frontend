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
import models.FullDetailsModel
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.data.Forms._

class FullDetailsForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  val nonEmptyCheck: String => Boolean = input => !input.isEmpty

  val fullDetailsForm = Form(
    mapping(
      "firstName" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck),
      "lastName" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck),
      "addressLineOne" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck),
      "addressLineTwo" -> optional(text),
      "townOrCity" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck),
      "county" -> optional(text),
      "postCode" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck),
      "country" -> text.verifying(Messages("errors.mandatory"), nonEmptyCheck)
    )(FullDetailsModel.apply)(FullDetailsModel.unapply)
  )
}
