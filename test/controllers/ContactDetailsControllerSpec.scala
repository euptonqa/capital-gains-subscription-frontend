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

import auth.{AuthorisedActions, CgtNROrganisation}
import connectors.KeystoreConnector
import data.{MessageLookup, TestUserBuilder}
import forms.ContactDetailsForm
import models.ContactDetailsModel
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import traits.ControllerTestSpec
import auth._
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class ContactDetailsControllerSpec extends ControllerTestSpec {

  val unauthorisedLoginUri = "some-url"
  val form = app.injector.instanceOf[ContactDetailsForm]

  def createMockActions(valid: Boolean = false, authContext: AuthContext = TestUserBuilder.userWithNINO): AuthorisedActions = {

    val mockActions = mock[AuthorisedActions]

    if (valid) {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedNROrganisationAction](0)
            val company = CgtNROrganisation(authContext)
            Action.async(action(company))
          }
        })
    }
    else {
      when(mockActions.authorisedNonResidentOrganisationAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(unauthorisedLoginUri)))
    }

    mockActions
  }

  def createMockKeyStore(data: Option[ContactDetailsModel]): KeystoreConnector = {

    val mockKeystore = mock[KeystoreConnector]

    when(mockKeystore.fetchAndGetFormData[ContactDetailsModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockKeystore
  }

  "Calling .contactDetails" when {

    "there is data stored in keystore" should {
      val model = ContactDetailsModel("Name", "01111111111", "test@example.com")
      val keystore = createMockKeyStore(Some(model))
      val actions = createMockActions(valid = true)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.contactDetails(FakeRequest("GET", ""))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the contact details page" in {
        document.title() shouldBe MessageLookup.ContactDetails.title
      }
    }

    "there is no data stored in keystore" should {
      val keystore = createMockKeyStore(None)
      val actions = createMockActions(valid = true)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.contactDetails(FakeRequest("GET", ""))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the contact details page" in {
        document.title() shouldBe MessageLookup.ContactDetails.title
      }
    }

    "the user is not authorised" should {
      val keystore = createMockKeyStore(None)
      val actions = createMockActions(valid = false)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.contactDetails(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'some-url'" in {
        redirectLocation(result) shouldBe Some("some-url")
      }
    }
  }

  "Calling .submitContactDetails" when {

    "posting a valid form" should {
      val keystore = createMockKeyStore(None)
      val actions = createMockActions(valid = true)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.submitContactDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        ("contactName", "Name"), ("telephone", "0111111111"), ("email", "test@example.com")))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to final confirmation page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.CorrespondenceAddressFinalConfirmationController.correspondenceAddressFinalConfirmation().url)
      }
    }

    "posting an invalid form" should {
      val keystore = createMockKeyStore(None)
      val actions = createMockActions(valid = true)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.submitContactDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        ("contactName", ""), ("telephone", ""), ("email", "")))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "load the contact details page" in {
        document.title() shouldBe MessageLookup.ContactDetails.title
      }
    }

    "the user is not authorised" should {
      val keystore = createMockKeyStore(None)
      val actions = createMockActions(valid = false)
      lazy val controller = new ContactDetailsController(mockConfig, form, messagesApi, keystore, actions)
      lazy val result = controller.submitContactDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        ("contactName", "Name"), ("telephone", "0111111111"), ("email", "test@example.com")))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to 'some-url'" in {
        redirectLocation(result) shouldBe Some("some-url")
      }
    }
  }
}
