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

import common.Keys
import models.{Enrolment, Identifier}
import play.api.inject.Injector
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class EnrolmentToCGTCheckSpec extends UnitSpec with WithFakeApplication {


  "Calling .checkEnrolments" should {
    "return true when supplied with a single element Sequence of Enrolments that includes the CGT enrolment" in {
      val enrolments = Seq(Enrolment(Keys.cGTEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")), ""))
      await(EnrolmentToCGTCheck.checkEnrolments(Some(enrolments))) shouldBe true
    }

    "return true when supplied with a multiple element Sequence of Enrolments that includes the CGT enrolment" in {
      val enrolments = Seq(
        Enrolment("Not the CGT Key", Seq(Identifier("DummyKey", "DummyValue")), ""),
        Enrolment(Keys.cGTEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")), "")
      )
      await(EnrolmentToCGTCheck.checkEnrolments(Some(enrolments))) shouldBe true
      }

    "return false when supplied with Sequence of Enrolments that does not include the CGT enrolment" in {
      val enrolments = Seq(Enrolment("Not the CGT Key", Seq(Identifier("DummyKey", "DummyValue")), ""))
      await(EnrolmentToCGTCheck.checkEnrolments(Some(enrolments))) shouldBe false
    }

    "return false when supplied with a None" in {
      await(EnrolmentToCGTCheck.checkEnrolments(None)) shouldBe false
    }
  }

  "Calling .checkAgentEnrolments" should {
    "return true when supplied with a single element Sequence of Enrolments that includes the Agent CGT enrolment" in {
      val enrolments = Seq(Enrolment(Keys.cgtAgentEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")),""))
      await(EnrolmentToCGTCheck.checkAgentEnrolments(Some(enrolments))) shouldBe true
    }

    "return true when supplied with a multiple element Sequence of Enrolments that includes the Agent CGT enrolment" in {
      val enrolments = Seq(
        Enrolment("Not the Agent CGT Key", Seq(Identifier("DummyKey", "DummyValue")), ""),
        Enrolment(Keys.cgtAgentEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")), "")
      )
      await(EnrolmentToCGTCheck.checkAgentEnrolments(Some(enrolments))) shouldBe true
    }

    "return false when supplied with a Sequence of Enrolments that does not include the Agent CGT enrolment" in {
      val enrolments = Seq(Enrolment("Not the Agent CGT Key", Seq(Identifier("DummyKey", "DummyValue")), ""))
      await(EnrolmentToCGTCheck.checkAgentEnrolments(Some(enrolments))) shouldBe false
    }

    "return false when supplied with a None" in {
      await(EnrolmentToCGTCheck.checkAgentEnrolments(None)) shouldBe false
    }
  }
}
