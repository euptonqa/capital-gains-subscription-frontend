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

import javax.inject.Inject

import connectors.SubscriptionConnector
import models.{CompanySubmissionModel, SubscriptionReference, UserFactsModel}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class SubscriptionService @Inject()(connector: SubscriptionConnector) {

  def getSubscriptionResponse(nino: String)(implicit hc: HeaderCarrier): Future[Option[SubscriptionReference]] = {
    connector.getSubscriptionResponse(nino)
  }

  def getSubscriptionNonResidentNinoResponse(nino:String)(implicit hc: HeaderCarrier): Future[Option[SubscriptionReference]] = {
    connector.getSubscriptionNonResidentNinoResponse(nino)
  }

  def getSubscriptionResponseGhost(userFacts: UserFactsModel)(implicit hc: HeaderCarrier): Future[Option[SubscriptionReference]] = {
    connector.getSubscriptionResponseGhost(userFacts)
  }

  def getSubscriptionResponseCompany(companySubmissionModel: CompanySubmissionModel)(implicit hc: HeaderCarrier): Future[Option[SubscriptionReference]] = {
    connector.getSubscriptionResponseCompany(companySubmissionModel)
  }
}
