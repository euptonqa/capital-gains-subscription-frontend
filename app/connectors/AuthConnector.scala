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

package connectors

import config.WSHttp
import models.AuthDataModel
import play.api.http.Status._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait AuthConnector extends ServicesConfig {

  def serviceUrl: String
  def authorityUri: String
  def http: HttpGet with HttpPost

  def getAuthResponse()(implicit hc: HeaderCarrier): Future[Option[AuthDataModel]] = {
    val getUrl = s"""$serviceUrl/$authorityUri"""
    http.GET[HttpResponse](getUrl).map {
      response => response.status match {
        case OK => {
          val confidenceLevel = (response.json \ "confidenceLevel").as[ConfidenceLevel]
          val uri = (response.json \ "uri").as[String]
          val credStrength = (response.json \ "credentialStrength").as[CredentialStrength]
          val affinityGroup = (response.json \ "affinityGroup").as[String]
          val nino = (response.json \ "accounts" \ "paye" \ "nino").as[String]

          Some(AuthDataModel(credStrength, affinityGroup, confidenceLevel, uri, nino))
        }
        case _ => None
      }
    }
  }
}

object AuthConnector extends AuthConnector{
  lazy val serviceUrl = baseUrl("auth")
  val authorityUri = "auth/authority"
  val http: HttpGet with HttpPost = WSHttp
}
