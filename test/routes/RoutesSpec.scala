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

package routes

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RoutesSpec extends UnitSpec with WithFakeApplication {

  "The URL for the incorrectAffinityGroup Action" should {
    "be equal to /capital-gains-subscription-frontend/subscribe/individual/invalid-user" in {
      val path = controllers.routes.IncorrectAffinityGroupController.incorrectAffinityGroup().url
      path shouldEqual "/capital-gains-subscription-frontend/subscribe/individual/invalid-user"
    }
  }

}
