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

package predicates

import java.net.URI

import builders.TestUserBuilder
import common.Keys
import helpers.EnrolmentToCGTCheck
import models.Enrolment
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.inject.Injector
import play.api.test.FakeRequest
import services.AuthorisationService
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class AgentEnrolmentPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val injector: Injector = fakeApplication.injector
  val enrolmentToCGTCheck: EnrolmentToCGTCheck = injector.instanceOf[EnrolmentToCGTCheck]

  val dummyUri = new URI("http://example.com")
  implicit val hc = mock[HeaderCarrier]

  def mockedPredicate(response: Option[Seq[Enrolment]]): AgentEnrolmentPredicate = {
    val mockService = mock[AuthorisationService]

    when(mockService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new AgentEnrolmentPredicate(dummyUri, mockService, enrolmentToCGTCheck)
  }

  "Instantiating the AgentEnrolmentPredicate" should {

    "return a visibility of true when enrolled in the service" in {
      val enrolments = Seq(Enrolment(Keys.cgtAgentEnrolmentKey, Seq(), ""), Enrolment("key", Seq(), ""))
      val predicate = mockedPredicate(Some(enrolments))
      val authContext = TestUserBuilder.create200ConfidenceUserAuthContext
      val result = predicate.apply(authContext, FakeRequest())

      await(result).isVisible shouldBe true
    }

    "return a visibility of false when not enrolled in the service" in {
      val enrolments = Seq(Enrolment("otherKey", Seq(), ""), Enrolment("key", Seq(), ""))
      val predicate = mockedPredicate(Some(enrolments))
      val authContext = TestUserBuilder.create200ConfidenceUserAuthContext
      val result = predicate.apply(authContext, FakeRequest())

      await(result).isVisible shouldBe false
    }

    "return a visibility of false when a None is returned for enrolments" in {
      val predicate = mockedPredicate(None)
      val authContext = TestUserBuilder.create200ConfidenceUserAuthContext
      val result = predicate.apply(authContext, FakeRequest())

      await(result).isVisible shouldBe false
    }

  }

}
