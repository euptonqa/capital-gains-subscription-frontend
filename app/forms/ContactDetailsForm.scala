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

import javax.inject.Inject

import models.ContactDetailsModel
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import uk.gov.hmrc.emailaddress.EmailAddress._

class ContactDetailsForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  val nonEmptyCheck: String => Boolean = input => !input.trim.isEmpty

  val verifyPhoneNumber: String => Boolean = number => {
    val regex = """^\+?[0-9]{0,24}$""".r
    if (nonEmptyCheck(number)) {
      regex.findFirstMatchIn(number.replaceAll(" ", "")).isDefined
    } else true
  }

  val contactDetailsForm = Form(
    mapping(
      "contactName" -> text
        .verifying(Messages("errors.required"), nonEmptyCheck),
      "telephone" -> text
        .verifying(Messages("errors.required"), nonEmptyCheck)
        .verifying(Messages("errors.telephone"), verifyPhoneNumber),
      "email" -> text
        .verifying(Messages("errors.email"), email => isValid(email))
    )(ContactDetailsModel.apply)(ContactDetailsModel.unapply)
  )

}
