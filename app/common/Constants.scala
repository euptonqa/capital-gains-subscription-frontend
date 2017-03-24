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

package common

object Constants {

  object AffinityGroup {
    val Agent = "Agent"
    val Individual = "Individual"
    val Organisation = "Organisation"
  }

  object InvalidUserTypes {
    val agent = "agent"
    val company = "company"
    val charity = "charity"
    val partnership = "partnership"
    val trust = "trust"
    val pensionTrust = "pensionTrust"

    val users = Seq(agent, company, charity, partnership, trust, pensionTrust, "")
  }

  object ErrorMessages {
    val businessDataNotFound: String = "Failed to retrieve registration details from BusinessCustomer keystore"
    val arnNotFound: String = "Agent Details retrieved did not contain an ARN"
    val failedToEnrolAgent: String = "Error returned from backend while attempting to enrol agent"
    val failedToEnrolIndividual: String = "DES responded with no subscription reference for Individual"
    val failedToEnrolCompany: String = "DES responded with no subscription reference for Company"
    val affinityGroupNotRetrieved: String = "Affinity group not retrieved"
  }
}
