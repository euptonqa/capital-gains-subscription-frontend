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

package helpers

import javax.inject.{Inject, Singleton}

import common.Keys.KeystoreKeys
import connectors.KeystoreConnector
import models.CallbackUrlModel
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LogicHelpers @Inject()(keystoreConnector: KeystoreConnector) {

  def saveCallbackUrl(url: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val model = CallbackUrlModel(url)
    keystoreConnector.saveFormData(KeystoreKeys.callbackUrlKey, model).map(_ => true)
  }
}
