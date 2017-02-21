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

package assets

object MessageLookup {

  object Common {
    val continue = "Continue"
    val change = "Change"
  }

  object Errors {
    val errorMandatory = "Please select an option"
    val errorRequired = "This field is required"
    val errorTelephone = "Please enter a valid telephone number"
    val errorEmail = "Please enter a valid email"
    val dummyError = "Dummy error message"
  }

  object ErrorSummary {
    val errorSummaryHeading = "This page has errors"
  }

  object InvalidAffinityGroup {
    val title = "You've signed in with the wrong type of account"
    val textOne = "This service only works with accounts set up for individuals."
    val textTwo = "If you want to continue as an individual, you'll need to sign out and log back in with an individual account."
    val signOut = "sign out"
    val textThreeAgent = "For more information about how to report taxable gains for your agencies client, see Self Assessment for Agents."
    val linkTextAgent = "Self Assessment for Agents"
    val textThreeCompany = "For more information about how to report taxable gains for your company, see Corporation Tax when you sell business assets."
    val linkTextCompany = "Corporation Tax when you sell business assets"
    val textThreeCharity = "For more information about how to report taxable gains for your registered charity, see Charities and tax."
    val linkTextCharity = "Charities and tax"
    val textThreePartnership = "For more information about how to report taxable gains for your partnership, see Partnerships and Capital Gains tax."
    val linkTextPartnership = "Partnerships and Capital Gains tax"
    val textThreeTrust = "For more information about how to report taxable gains for your trust, see Trusts and Capital Gains tax."
    val linkTextTrust = "Trusts and Capital Gains tax"
    val textThreePensionTrust = "For more information about how to report taxable gains for your pension trust, see Pension trustees: investments and tax."
    val linkTextPensionTrust = "Pension trustees: investments and tax"
  }

  object EnterCorrespondenceAddress {
    val title = "Your correspondence address"
    val addressLineOne = "Address line 1"
    val addressLineTwo = "Address line 2"
    val addressLineThree = "Address line 3 (optional)"
    val addressLineFour = "Address line 4 (optional)"
    val country = "Country"
    val postcode = "Postcode"
  }

  object UserDetails {
    val title = "Enter your name and address"
    val secondHeader = "Contact address"
    val firstName = "First name"
    val lastName = "Last name"
    val addressLineOne = "Building and street"
    val addressLineTwo = "Building and street line two"
    val townOrCity = "Town or city"
    val county = "County"
    val postCode = "Postcode"
    val country = "Country"
  }

  object ContactDetails {
    val title = "Capital Gains Tax contact details"
    val text = "The person responsible for Capital Gains Tax related queries."
    val contactName = "Contact name"
    val telephone = "Telephone"
    val email = "Email"
  }

  object UseRegisteredAddress {
    val title = "Your correspondence address"
    val question = "Is this your correspondence address?"
  }

  object OrganisationType {
    val title = "What type of organisation are you?"
    val question = "What type of organisation are you?"
    val company = "Limited Company"
    val charity = "Charity"
    val partnership = "Partnerships"
    val trust = "Trusts"
    val pensionTrust = "Pension trust administrator"
  }

  object CGTSubscriptionConfirm {
    val title = "Your Capital Gains Tax ID"
    val writeDown = "You must write this down."
    val forgetGGID = "If you forget your Government Gateway account details we can use this ID to help you retrieve them."
    val whatNext = "What happens next?"
    val whatNextContent = "Continue to report your Capital Gains Tax using our online form."
    val figuresReady = "You should have already done your Capital Gains Tax calculation and have your figures ready."
  }

  object ReviewBusinessDetails {
    val title = "Review business details"
    val subtitle = "Your Capital Gains Tax account will be created with these details."
    val colHeading = "Name"
    val registeredAddress = "Registered address"
    val correspondenceAddress = "Correspondence address"
    val contactDetails = "Capital Gains Tax contact details"
    val registerConfirm = "Register and confirm"
  }

  object SetupYourAgency {
    val title = "Set up your agency"
    val leadParagraph = "Before you can submit Capital Gains Tax returns for your clients you'll need to set up your agency" +
      " for this service. You only need to do this once."
    val listOne = "Enter your agency's registered name and unique tax reference (UTR)."
    val listTwo = "After setting up your agency, you can add your clients."
  }
}
