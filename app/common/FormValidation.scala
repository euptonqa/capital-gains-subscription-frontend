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

object FormValidation {

  val nonEmptyCheck: String => Boolean = input => !input.isEmpty

  val textToOptional: String => Option[String] = input =>
    if (input.isEmpty) None
    else Some(input)

  val optionalToText: Option[String] => String = {
    case Some(data) => data
    case _ => ""
  }

  def postcodeCheck(postcode: Option[String], countryCode: String): Boolean = {
    if (countryCode =="GB") postcode.isDefined
    else true
  }
}
