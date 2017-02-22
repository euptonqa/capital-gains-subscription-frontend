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

import connectors.{FailedAgentEnrolmentResponse, SubscriptionConnector, SuccessAgentEnrolmentResponse}
import models.AgentSubmissionModel
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class AgentServiceSpec extends UnitSpec with MockitoSugar {

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  "Calling AgentService .enrolAgent" should {

    "with a valid request" should {

      val agentSubmissionModel: AgentSubmissionModel = AgentSubmissionModel("SAP", "ARN123456")
      val mockConnector = mock[SubscriptionConnector]

      when(mockConnector.enrolAgent(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(SuccessAgentEnrolmentResponse))

      val service = new AgentService(mockConnector)

      val result = service.getAgentEnrolmentResponse(agentSubmissionModel)

      "return a SuccessAgentEnrolmentResponse" in {
        await(result) shouldEqual SuccessAgentEnrolmentResponse
      }
    }
  }

  "with an invalid request" should {

    val agentSubmissionModel: AgentSubmissionModel = AgentSubmissionModel("SAP", "ARN123456")
    val mockConnector = mock[SubscriptionConnector]

    when(mockConnector.enrolAgent(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(FailedAgentEnrolmentResponse))

    val service = new AgentService(mockConnector)

    val result = service.getAgentEnrolmentResponse(agentSubmissionModel)

    "return a FailedAgentEnrolmentResponse" in {
      await(result) shouldEqual FailedAgentEnrolmentResponse
    }
  }
}
