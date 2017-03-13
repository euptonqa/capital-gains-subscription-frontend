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

import config.AppConfig
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import common.Constants.{AffinityGroup, InvalidUserTypes}
import data.MessageLookup
import exceptions.AffinityGroupNotFoundException
import forms.OrganisationForm
import services.AuthorisationService
import traits.FakeRequestHelper

class OrganisationTypeControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper {

  def setupTarget(affinityGroup: Option[String]): OrganisationTypeController = {

    val injector: Injector = fakeApplication.injector

    def appConfig: AppConfig = injector.instanceOf[AppConfig]

    def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

    def organisationForm: OrganisationForm = injector.instanceOf[OrganisationForm]

    val authService: AuthorisationService = mock[AuthorisationService]

    when(authService.getAffinityGroup(ArgumentMatchers.any())).thenReturn(affinityGroup)

    new OrganisationTypeController(appConfig, authService, organisationForm, messagesApi)
  }

  "Calling .organisationType" when {

    "provided with an valid Organisation affinityGroup" should {

      lazy val target = setupTarget(Some(AffinityGroup.Organisation))
      lazy val result = target.organisationType(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the organisation type page" in {
        Jsoup.parse(bodyOf(result)).title() shouldBe MessageLookup.OrganisationType.title
      }
    }

    "provided with an valid Agent affinityGroup" should {

      lazy val target = setupTarget(Some(AffinityGroup.Agent))
      lazy val result = target.organisationType(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for agents page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=agent")
      }
    }

    "provided with an invalid affinityGroup" should {

      lazy val target = setupTarget(Some("Testing"))
      lazy val requestResult = target.organisationType(fakeRequest)

      "throw an AffinityGroupNotFoundException" in {

        lazy val result = try { await(requestResult) }
        catch {
          case e: AffinityGroupNotFoundException => true
          case _: Throwable => false
        }

        await(result) shouldEqual true
      }
    }
  }

  "Calling .submitOrganisationType" when {

    "provided with an valid company organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", InvalidUserTypes.company)))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for company page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=company")
      }
    }

    "provided with an valid charity organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", InvalidUserTypes.charity)))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for charity page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=charity")
      }
    }

    "provided with an valid partnership organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", InvalidUserTypes.partnership)))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for partnership page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=partnership")
      }
    }

    "provided with an valid trust organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", InvalidUserTypes.trust)))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for trust page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=trust")
      }
    }

    "provided with an valid pensionTrust organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", InvalidUserTypes.pensionTrust)))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "load the invalid affinity group for pensionTrust page" in {
        redirectLocation(result) shouldBe Some("/capital-gains-tax/subscription/individual/invalid-user?userType=pensionTrust")
      }
    }

    "provided with an invalid organisation type" should {

      lazy val target = setupTarget(None)
      lazy val result = target.submitOrganisationType(fakeRequestToPOSTWithSession(("organisationType", "invalid type")))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "return the user to the organisation type page" in {
        Jsoup.parse(bodyOf(result)).title shouldEqual MessageLookup.OrganisationType.title
      }
    }
  }
}
