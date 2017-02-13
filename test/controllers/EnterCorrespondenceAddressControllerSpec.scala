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

import assets.{ControllerTestSpec, MessageLookup}
import auth.{AuthorisedActions, CgtNROrganisation}
import builders.TestUserBuilder
import config.{AppConfig, BusinessCustomerSessionCache, SubscriptionSessionCache}
import connectors.KeystoreConnector
import forms.CorrespondenceAddressForm
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import types.AuthenticatedNROrganisationAction
import uk.gov.hmrc.play.frontend.auth.AuthContext

class EnterCorrespondenceAddressControllerSpec extends ControllerTestSpec {

  val testOnlyUnauthorisedLoginUri = "just-a-test"

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.strongUserAuthContext): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](0)
            val organisation = CgtNROrganisation(authContext)
            Action.async(action(organisation))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }
    mockActions
  }

  def createMockKeystoreConnector: KeystoreConnector = {
    lazy val config: AppConfig = mock[AppConfig]
    lazy val subscriptionSessionCache: SubscriptionSessionCache = mock[SubscriptionSessionCache]
    lazy val businessCustomerSessionCache: BusinessCustomerSessionCache = mock[BusinessCustomerSessionCache]

    new KeystoreConnector(config, subscriptionSessionCache, businessCustomerSessionCache)
  }

  "Calling .enterCorrespondenceAddress" when {

    "using correct authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val action = createMockActions(valid = true)
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, keystoreConnector, action, messagesApi)
      lazy val result = controller.enterCorrespondenceAddress(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "display the user details view" in {
        document.title() shouldBe MessageLookup.EnterCorrespondenceAddress.title
      }
    }

    "using incorrect authorisation" should {
      val fakeRequest = FakeRequest("GET", "/")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val action = createMockActions()
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, keystoreConnector, action, messagesApi)
      lazy val result = controller.enterCorrespondenceAddress(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test uri" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }
  }

  "Calling .submitCorrespondenceAddress" when {

    "authorised but with an invalid form" should {

      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody()
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val action = createMockActions(valid = true)
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, keystoreConnector, action, messagesApi)
      lazy val result = controller.submitCorrespondenceAddress(fakeRequest)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "return to the user details page" in {
        document.title() shouldBe MessageLookup.EnterCorrespondenceAddress.title
      }
    }

    "authorised with a valid form" should {

      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody("addressLineOne" -> "XX Fake Lane", "addressLineTwo" -> "Fake Town", "addressLineThree" -> "Fake City",
          "addressLineFour" -> "Fake County", "country" -> "Fakeland", "postcode" -> "XX22 1XX")
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val action = createMockActions(valid = true)
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, keystoreConnector, action, messagesApi)
      lazy val result = controller.submitCorrespondenceAddress(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the confirm contact details page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ContactDetailsController.contactDetails().url)
      }
    }

    "un-authorised" should {

      val fakeRequest = FakeRequest("POST", "/")
        .withFormUrlEncodedBody()
      lazy val form = app.injector.instanceOf[CorrespondenceAddressForm]
      lazy val keystoreConnector = createMockKeystoreConnector
      lazy val action = createMockActions()
      lazy val controller = new EnterCorrespondenceAddressController(mockConfig, form, keystoreConnector, action, messagesApi)
      lazy val result = controller.submitCorrespondenceAddress(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test uri" in {
        redirectLocation(result).get.toString shouldBe "just-a-test"
      }
    }
  }
}
