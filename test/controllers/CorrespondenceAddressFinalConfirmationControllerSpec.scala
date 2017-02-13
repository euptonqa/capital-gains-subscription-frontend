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

package controllers

import uk.gov.hmrc.play.test.UnitSpec

class CorrespondenceAddressFinalConfirmationControllerSpec extends UnitSpec {

  "Calling .correspondenceAddressFinalConfirmation" when {

    "the business data, correspondence address and contact details are supplied" should {

      "return a status of 200" in {

      }

      "load the final confirmation page" in {

      }
    }

    "no business data is found" should {

      "return a status of 400" in {

      }
    }

    "no correspondence address is found" should {

      "return a status of 400" in {

      }
    }

    "no contact details are found" should {

      "return a status of 400" in {

      }
    }

    "the user is unauthorised" should {

      "return a status of 300" in {

      }

      "redirect to 'some-url'" in {

      }
    }
  }

  "Calling .submitCorrespondenceAddressFinalConfirmation" when {

  }

}
