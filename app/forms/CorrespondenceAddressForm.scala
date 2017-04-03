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

import common.FormValidation
import models.CompanyAddressModel
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}

class CorrespondenceAddressForm @Inject()(val messagesApi: MessagesApi, formValidation: FormValidation) extends I18nSupport {

  val correspondenceAddressForm = Form(
    mapping(
      "addressLineOne" -> text.verifying(Messages("errors.required"), formValidation.nonEmptyCheck).transform(formValidation.textToOptional, formValidation.optionalToText),
      "addressLineTwo" -> text.verifying(Messages("errors.required"), formValidation.nonEmptyCheck).transform(formValidation.textToOptional, formValidation.optionalToText),
      "addressLineThree" -> text.transform(formValidation.textToOptional, formValidation.optionalToText),
      "addressLineFour" -> text.transform(formValidation.textToOptional, formValidation.optionalToText),
      "postcode" -> text.verifying(Messages("errors.required"), formValidation.nonEmptyCheck).transform(formValidation.textToOptional, formValidation.optionalToText),
      "country" -> formValidation.countryCodeCheck
    )(CompanyAddressModel.apply)(CompanyAddressModel.unapply)
  )
}
