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

import com.google.inject.{Inject, Singleton}
import config.WSHttp
import models.{AuthorisationDataModel, Enrolment}
import play.api.http.Status._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.frontend.auth.connectors.domain._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthorisationConnector @Inject()(http: WSHttp) extends ServicesConfig {

  lazy val serviceUrl: String = baseUrl("auth")
  val authorityUri: String = "auth/authority"

  def getAuthResponse()(implicit hc: HeaderCarrier): Future[Option[AuthorisationDataModel]] = {
    val getUrl = s"""$serviceUrl/$authorityUri"""
    http.GET[HttpResponse](getUrl).map {
      response =>
        response.status match {
          case OK => {
            val confidenceLevel = (response.json \ "confidenceLevel").as[ConfidenceLevel]
            val uri = (response.json \ "uri").as[String]
            val credStrength = (response.json \ "credentialStrength").as[CredentialStrength]
            val affinityGroup = (response.json \ "affinityGroup").as[String]
            val accounts = (response.json \ "accounts").as[Accounts]

            Some(AuthorisationDataModel(credStrength, affinityGroup, confidenceLevel, uri, accounts))
          }
          case _ => None
        }
    }
  }

  def getEnrolmentsResponse(uri: String)(implicit hc: HeaderCarrier): Future[Option[Seq[Enrolment]]] = {
    val getUrl = s"$serviceUrl$uri/enrolments"
    http.GET[HttpResponse](getUrl).map {
      response =>
        response.status match {
          case OK => Some(response.json.as[Seq[Enrolment]])
          case _ => None
        }
    }
  }
}
