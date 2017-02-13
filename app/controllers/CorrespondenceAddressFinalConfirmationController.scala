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

import javax.inject.{Inject, Singleton}

import auth.AuthorisedActions
import common.Keys
import connectors.KeystoreConnector
import models.{CompanyAddressModel, CompanySubmissionModel, ContactDetailsModel, ReviewDetails}
import play.api.mvc.{Action, AnyContent, Result}
import services.SubscriptionService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressFinalConfirmationController @Inject()(actions: AuthorisedActions,
                                                                 subscriptionService: SubscriptionService,
                                                                 keystoreConnector: KeystoreConnector) extends FrontendController {

  val correspondenceAddressFinalConfirmation = TODO

  val submitCorrespondenceAddressFinalConfirmation: Action[AnyContent] = actions.authorisedNonResidentOrganisationAction {
    implicit user =>
      implicit request =>

        def successAction(companyAddressModel: CompanyAddressModel, contactDetailsModel: ContactDetailsModel): Future[Result] = {
          val result = {

            def handleBusinessData(reviewDetails: Option[ReviewDetails], companyAddressModel: CompanyAddressModel) = {
              reviewDetails match {
                case Some(details) => Future.successful(CompanySubmissionModel(Some(details.safeId),
                  Some(contactDetailsModel), Some(companyAddressModel), Some(details.businessAddress)))
                case _ => Future.failed(new Exception("Details not found"))
              }
            }

            for {
              businessData <- keystoreConnector.fetchAndGetBusinessData()
              submissionModel <- handleBusinessData(businessData, companyAddressModel)
              cgtRef <- subscriptionService.getSubscriptionResponseCompany(submissionModel)
            } yield cgtRef
          }

          result.map { reference =>
            Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(reference.get.cgtRef))
          } recoverWith {
            case error => Future.successful(InternalServerError(error.getMessage))
          }
        }

        val companyAddress = keystoreConnector.fetchAndGetFormData[CompanyAddressModel](Keys.KeystoreKeys.correspondenceAddressKey)
        val contactDetails = keystoreConnector.fetchAndGetFormData[ContactDetailsModel](Keys.KeystoreKeys.contactDetailsKey)

        def getResult(companyAddressModel: Option[CompanyAddressModel], contactDetailsModel: Option[ContactDetailsModel]): Future[Result] = {
          (companyAddressModel, contactDetailsModel) match {
            case (Some(address), Some(details)) => successAction(address, details)
            case _ => Future.successful(BadRequest(""))
          }
        }

        for {
          address <- companyAddress
          details <- contactDetails
          result <- getResult(address, details)
        } yield result
  }
}
