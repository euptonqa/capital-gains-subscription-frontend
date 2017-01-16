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

package forms

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import forms.OrganisationForm._
import models.OrganisationModel
import common.Constants.InvalidUserTypes._
import assets.MessageLookup.{Errors => messages}

class OrganisationFormSpec extends UnitSpec with WithFakeApplication {

  "Creating the form from a model" should {

    "create an empty form when the model is empty" in {
      val form = organisationForm
      form.data.isEmpty shouldBe true
    }

    "create a map with the option 'agent' when the model contains a 'agent'" in {
      val model = OrganisationModel(agent)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(agent)
    }

    "create a map with the option 'company' when the model contains a 'company'" in {
      val model = OrganisationModel(company)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(company)
    }

    "create a map with the option 'charity' when the model contains a 'charity'" in {
      val model = OrganisationModel(charity)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(charity)
    }

    "create a map with the option 'partnership' when the model contains a 'partnership'" in {
      val model = OrganisationModel(partnership)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(partnership)
    }

    "create a map with the option 'trust' when the model contains a 'trust'" in {
      val model = OrganisationModel(trust)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(trust)
    }

    "create a map with the option 'pensionTrust' when the model contains a 'pensionTrust'" in {
      val model = OrganisationModel(pensionTrust)
      val form = organisationForm.fill(model)
      form.data.get("organisationType") shouldBe Some(pensionTrust)
    }
  }

  "Creating the form from a valid map" should {

    "create a model containing 'agent' when provided with a map containing 'agent'" in {
      val map = Map("organisationType" -> agent)
      val form = organisationForm.bind(map)
      form.value shouldBe Some(OrganisationModel(agent))
    }
  }

  "Creating the form from an invalid map" when {

    "no data is provided" should {
      lazy val map = Map("organisationType" -> "")
      lazy val form = organisationForm.bind(map)

      "produce a form with errors" in {
        form.hasErrors shouldBe true
      }

      "contain only one error" in {
        form.errors.length shouldBe 1
      }

      s"contain the error message ${messages.errorMandatory}" in{
        form.errors.head.message shouldBe messages.errorMandatory
      }
    }

    "incorrect data is provided" should {
      lazy val map = Map("organisationType" -> "badData")
      lazy val form = organisationForm.bind(map)

      "produce a form with errors" in {
        form.hasErrors shouldBe true
      }

      "contain only one error" in {
        form.errors.length shouldBe 1
      }

      s"contain the error message ${messages.errorMandatory}" in{
        form.errors.head.message shouldBe messages.errorMandatory
      }
    }
  }
}

