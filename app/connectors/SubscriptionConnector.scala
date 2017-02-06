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

import javax.inject.{Inject, Singleton}
import config.{AppConfig, WSHttp}
import models.{UserFactsModel, SubscriptionReference}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SubscriptionConnector @Inject()(http: WSHttp, appConfig: AppConfig) extends ServicesConfig {

  lazy val serviceUrl: String =  appConfig.subscription
  val subscriptionUrl: String = "subscribe/resident/individual"

  def getSubscriptionResponse(nino: String)(implicit hc: HeaderCarrier): Future[Option[String]] = {
    val getUrl = s"""$serviceUrl/$subscriptionUrl/?nino=$nino"""
    http.GET[HttpResponse](getUrl).map{
      response =>
        response.status match {
          case OK =>
            Some(response.json.as[String])
          case _ => None
        }
    }
  }

  def getSubscriptionResponseGhost(userFacts: UserFactsModel)(implicit hc: HeaderCarrier): Future[Option[SubscriptionReference]] = {

    val postUrl =s"""$serviceUrl/$subscriptionUrl/"""
    http.POST[JsValue, HttpResponse](postUrl, Json.toJson(userFacts)).map{
      response =>
        response.status match {
          case OK =>
            Some(response.json.as[SubscriptionReference])
          case _=> None
        }
    }
  }

}
