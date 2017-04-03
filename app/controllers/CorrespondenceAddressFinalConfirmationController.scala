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
import common.{CountriesMatcher, Keys}
import common.Constants.ErrorMessages._
import common.Keys.KeystoreKeys
import config.AppConfig
import connectors.KeystoreConnector
import models.{CompanyAddressModel, CompanySubmissionModel, ContactDetailsModel, ReviewDetails}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.SubscriptionService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressFinalConfirmationController @Inject()(appConfig: AppConfig,
                                                                 val messagesApi: MessagesApi,
                                                                 actions: AuthorisedActions,
                                                                 subscriptionService: SubscriptionService,
                                                                 keystoreConnector: KeystoreConnector,
                                                                 implicit val countriesMatcher: CountriesMatcher) extends FrontendController with I18nSupport {

  val correspondenceAddressFinalConfirmation: Action[AnyContent] = actions.authorisedNonResidentOrganisationAction { implicit user =>
    implicit request =>

      val businessData = keystoreConnector.fetchAndGetBusinessData()

      val addressData: Future[Option[CompanyAddressModel]] = keystoreConnector.fetchAndGetFormData[CompanyAddressModel](KeystoreKeys.correspondenceAddressKey)

      val contactDetailsData: Future[Option[ContactDetailsModel]] = keystoreConnector.fetchAndGetFormData[ContactDetailsModel](KeystoreKeys.contactDetailsKey)

      def yieldBusinessData = {
        for {
          data <- businessData
          address <- addressData
          contactDetails <- contactDetailsData
        } yield {
          Ok(views.html.reviewBusinessDetails(appConfig, data.get.businessAddress, address.get, data.get.businessName, contactDetails.get))
        }
      }

      yieldBusinessData.recoverWith {
        case exception: Exception => Logger.warn(businessDataNotFound)
          Future.failed(new Exception(businessDataNotFound))
      }
  }

  val submitCorrespondenceAddressFinalConfirmation: Action[AnyContent] = actions.authorisedNonResidentOrganisationAction {
    implicit user =>
      implicit request =>

        def successAction(companyAddressModel: CompanyAddressModel, contactDetailsModel: ContactDetailsModel): Future[Result] = {
          val result = {

            def handleBusinessData(reviewDetails: Option[ReviewDetails], companyAddressModel: CompanyAddressModel) = {
              reviewDetails match {
                case Some(details) => Future.successful(CompanySubmissionModel(Some(details.safeId),
                  Some(contactDetailsModel), Some(companyAddressModel), Some(details.businessAddress)))
                case _ => Logger.warn(businessDataNotFound)
                  Future.failed(new Exception(businessDataNotFound))
              }
            }

            for {
              businessData <- keystoreConnector.fetchAndGetBusinessData()
              submissionModel <- handleBusinessData(businessData, companyAddressModel)
              cgtRef <- subscriptionService.getSubscriptionResponseCompany(submissionModel)
            } yield cgtRef
          }

          result.map {
            case Some(x) => Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(x.cgtRef))
            case None => throw new Exception(failedToEnrolCompany)
          } recoverWith {
            case error => Future.failed(error)
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
