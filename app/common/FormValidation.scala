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

package common

import javax.inject.Inject

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}

class FormValidation @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  val nonEmptyCheck: String => Boolean = input => !input.isEmpty

  val textToOptional: String => Option[String] = input =>
    if (input.isEmpty) None
    else Some(input)

  val optionalToText: Option[String] => String = {
    case Some(data) => data
    case _ => ""
  }



  def countryCodeCheck: Mapping[String] = {
    val countryCode = """[A-Z]{2}""".r
    val countryCodeCheckConstraint: Constraint[String] =
      Constraint("constraints.countryCode")({
        text =>
          val error = text match {
            case countryCode() => Nil
            case _ => Seq(ValidationError(Messages("errors.country")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(countryCodeCheckConstraint)
  }
}
