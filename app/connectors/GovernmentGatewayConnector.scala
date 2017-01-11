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

import com.google.inject.Inject
import config.WSHttp
import models.Enrolment
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import play.api.http.Status._
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class GovernmentGatewayConnector @Inject()() extends ServicesConfig {

  lazy val serviceUrl = baseUrl("auth")
  val authorityUri = "auth/authority"
  val http: HttpGet with HttpPost = WSHttp

  def getEnrolments(uri: String)(implicit hc: HeaderCarrier): Future[Option[Seq[Enrolment]]] = {
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
