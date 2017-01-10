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
import enums.IdentityVerificationResult.IdentityVerificationResult
import play.api.libs.json.Json
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IdentityVerificationConnector extends IdentityVerificationConnector with ServicesConfig {
  override val serviceUrl = baseUrl("identity-verification")
  override def http: HttpGet = WSHttp
}

trait IdentityVerificationConnector {

  val serviceUrl: String

  def http: HttpGet

  private def url(journeyId: String) = s"$serviceUrl/mdtp/journey/journeyId/$journeyId"

  private[connectors] case class IdentityVerificationResponse(result: IdentityVerificationResult)

  private implicit val formats = Json.format[IdentityVerificationResponse]

  def identityVerificationResponse(journeyId: String)(implicit hc: HeaderCarrier): Future[IdentityVerificationResult] = {
    http.GET[HttpResponse](url(journeyId)).flatMap { httpResponse =>
      Future.successful(httpResponse.json.as[IdentityVerificationResult])
    }
  }
}
