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

///*
// * Copyright 2017 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package routes
//
//import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
//
//class RoutesSpec extends UnitSpec with WithFakeApplication {
//
//  "The URL for the incorrectAffinityGroup Action" should {
//    "be equal to /capital-gains-tax/subscription/individual/invalid-user?userType=company" in {
//      val path = controllers.routes.IncorrectAffinityGroupController.incorrectAffinityGroup("company").url
//      path shouldEqual "/capital-gains-tax/subscription/individual/invalid-user?userType=company"
//    }
//  }
//
//  "The URL for the organisationType Action" should {
//    "be equal to /capital-gains-tax/subscription/individual/organisation-type" in {
//      val path = controllers.routes.OrganisationTypeController.organisationType().url
//      path shouldEqual "/capital-gains-tax/subscription/individual/organisation-type"
//    }
//  }
//
//  "The URL for the nonResidentIndividualSubscription Action" should {
//    "be equal to /capital-gains-tax/subscription/non-resident/individual" in {
//      val path = controllers.routes.NonResidentIndividualSubscriptionController.nonResidentIndividualSubscription("/test/route").url
//      path shouldEqual "/capital-gains-tax/subscription/non-resident/individual?url=%2Ftest%2Froute"
//    }
//  }
//
//  "The URL for the submitOrganisationType Action" should {
//    "be equal to /capital-gains-tax/subscription/individual/organisation-type" in {
//      val path = controllers.routes.OrganisationTypeController.submitOrganisationType().url
//      path shouldEqual "/capital-gains-tax/subscription/individual/organisation-type"
//    }
//  }
//
//  "The URL for the residentIndividualSubscription action" should {
//    "be equal to /capital-gains-tax/subscription/resident/individual" in {
//      val path = controllers.routes.ResidentIndividualSubscriptionController.residentIndividualSubscription("/test/route").url
//      path shouldEqual "/capital-gains-tax/subscription/resident/individual?url=%2Ftest%2Froute"
//    }
//  }
//
//  "The URL for the CGTSubscriptionConfirmation action" should {
//    "be equal to /capital-gains-tax/subscription/confirmation" in {
//      val path = controllers.routes.CGTSubscriptionController.confirmationOfSubscription("cgtRef").url
//      path shouldEqual "/capital-gains-tax/subscription/confirmation?cgtReference=cgtRef"
//    }
//  }
//
//  "The URL for the submitCGTSubscriptionConfirmation action" should {
//    "be equal to /capital-gains-tax/subscription/confirmation" in {
//      val path = controllers.routes.CGTSubscriptionController.submitConfirmationOfSubscription().url
//      path shouldEqual "/capital-gains-tax/subscription/confirmation"
//    }
//  }
//
//  "The URL for the company action" should {
//    "be equal to /capital-gains-tax/subscription/company" in {
//      val path = controllers.routes.CompanyController.subscribe("/test/route").url
//      path shouldEqual "/capital-gains-tax/subscription/company?url=%2Ftest%2Froute"
//    }
//  }
//
//  "The URL for the businessAddressConfirm action" should {
//    "be equal to /capital-gains-tax/subscription/correspondence-address-confirm" in {
//      val path = controllers.routes.CorrespondenceAddressConfirmController.correspondenceAddressConfirm().url
//
//      path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-confirm"
//    }
//  }
//
//  "The URL for the submitBusinessAddressConfirm action" should {
//    "be equal to /capital-gains-tax/subscription/correspondence-address-confirm" in {
//      val path = controllers.routes.CorrespondenceAddressConfirmController.submitCorrespondenceAddressConfirm().url
//
//      path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-confirm"
//    }
//  }
//
//  "The URL for the enterCorrespondenceAddress action" should {
//    "be equal to /capital-gains-tax/subscription/company/correspondence-address-enter" in {
//      val path = controllers.routes.EnterCorrespondenceAddressController.enterCorrespondenceAddress().url
//
//      path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-enter"
//    }
//  }
//
//  "The URL for the submitCorrespondenceAddress action" should {
//    "be equal to /capital-gains-tax/subscription/company/correspondence-address-enter" in {
//      val path = controllers.routes.EnterCorrespondenceAddressController.submitCorrespondenceAddress().url
//
//      path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-enter"
//    }
//  }
//
//  "The URL for the correspondenceAddressFinalConfirmation action" should {
//    "be equal to /capital-gains-tax/subscription/company/correspondence-address-final-confirmation" in {
//      val path = controllers.routes.CorrespondenceAddressFinalConfirmationController.correspondenceAddressFinalConfirmation().url
//        path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-final-confirmation"
//    }
//  }
//
//  "The URL for the submitCorrespondenceAddressFinalConfirmation action" should {
//    "be equal to /capital-gains-tax/subscription/company/correspondence-address-final-confirmation" in {
//      val path = controllers.routes.CorrespondenceAddressFinalConfirmationController.correspondenceAddressFinalConfirmation().url
//
//      path shouldEqual "/capital-gains-tax/subscription/company/correspondence-address-final-confirmation"
//    }
//  }
//
//  "The URL for the agent action" should {
//    "be equal to /capital-gains-tax/subscription/agent/subscribe" in {
//      val path = controllers.routes.AgentController.agent("/test/route").url
//
//      path shouldEqual "/capital-gains-tax/subscription/agent/subscribe?url=%2Ftest%2Froute"
//    }
//  }
//
//  "The URL for the registered agent action" should {
//    "be equal to /capital-gains-tax/subscription/agent/registered/subscribe" in {
//      val path = controllers.routes.AgentController.registeredAgent().url
//
//      path shouldEqual "/capital-gains-tax/subscription/agent/registered/subscribe"
//    }
//  }
//}
