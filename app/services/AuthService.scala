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

package services

import models.AuthDataModel
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{ConfidenceLevel, CredentialStrength}


object AuthService {

  def getAuthDataModel(authData: JsValue): AuthDataModel = {
    val confidenceLevel = (authData \ "confidenceLevel").as[ConfidenceLevel]
    val uri = (authData \ "uri").as[String]
    val credStrength = (authData \ "credentialStrength").as[CredentialStrength]
    val affinityGroup = (authData \ "affinityGroup").as[String]
    val nino = (authData \ "accounts" \ "paye" \ "nino").asOpt[String]

    AuthDataModel(credStrength, affinityGroup, confidenceLevel, uri, nino)

  }
}
