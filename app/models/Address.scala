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

package models

import play.api.libs.json.Json

case class Address(line_1: String,
                   line_2: String,
                   line_3: Option[String],
                   line_4: Option[String],
                   postcode: Option[String] = None,
                   country: String)

object Address {
  implicit val formats = Json.format[Address]
  implicit val converts: Address => CompanyAddressModel = address => {
    CompanyAddressModel(Some(address.line_1), Some(address.line_2), address.line_3, address.line_4, address.postcode, address.country)
  }
}
