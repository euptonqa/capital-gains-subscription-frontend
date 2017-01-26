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
  }

  object Errors {
    val errorMandatory = "Please select an option"
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
}
