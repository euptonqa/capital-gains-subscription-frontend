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

import com.google.inject.{Inject, Singleton}
import connectors.SubscriptionConnector
import models.FullDetails
import uk.gov.hmrc.play.http.HeaderCarrier

class SubscriptionService @Inject()(connector: SubscriptionConnector) {

  def getSubscriptionResponse(nino: String)(implicit hc: HeaderCarrier) = {
    connector.getSubscriptionResponse(nino)
  }

  def getSubscriptionResponseGhost(fullDetails: FullDetails)(implicit hc: HeaderCarrier) = {
    connector.getSubscriptionResponseGhost(fullDetails)
  }
}
