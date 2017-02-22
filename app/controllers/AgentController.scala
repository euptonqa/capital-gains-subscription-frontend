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

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import auth.{AuthorisedActions, CgtAgent}
import config.AppConfig
import connectors.{KeystoreConnector, SuccessAgentEnrolmentResponse}
import helpers.EnrolmentToCGTCheck
import models.{AgentSubmissionModel, ReviewDetails}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{AgentService, AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AgentController @Inject()(appConfig: AppConfig,
                                authorisedActions: AuthorisedActions,
                                agentService: AgentService,
                                sessionService: KeystoreConnector,
                                authService: AuthorisationService,
                                enrolmentToCGTCheck: EnrolmentToCGTCheck,
                                subscriptionService: SubscriptionService,
                                val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val businessCustomerFrontendUrl: String = appConfig.businessCompanyFrontendRegister
  private val businessDataNotFoundError: String = "Failed to retrieve registration details from BusinessCustomer keystore"
  private val arnNotFoundError: String = "Agent Details retrieved did not contain an ARN"
  private val failedToEnrolError: String = "Error returned from backend while attempting to enrol agent"

  val agent: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>

        def checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user: CgtAgent, isEnrolled: Boolean)(implicit hc: HeaderCarrier): Future[Result] = {
          if (isEnrolled) Future.successful(Redirect("http://www.gov.uk"))
          else Future.successful(Ok(views.html.setupYourAgency(appConfig)))
        }

        for {
          enrolments <- authService.getEnrolments
          isEnrolled <- enrolmentToCGTCheck.checkAgentEnrolments(enrolments)
          redirect <- checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user, isEnrolled)
        } yield redirect
  }

  private[controllers] def handleBusinessData()(implicit hc: HeaderCarrier): Future[ReviewDetails] = {
    sessionService.fetchAndGetBusinessData().flatMap {
      case Some(details) => Future.successful(details)
      case None => Logger.warn(businessDataNotFoundError)
        Future.failed(new Exception(businessDataNotFoundError))
    }
  }

  private[controllers] def constructAgentSubmissionModel(businessData: ReviewDetails): Future[AgentSubmissionModel] = {
    if (businessData.agentReferenceNumber.isDefined) Future.successful(AgentSubmissionModel(businessData.safeId, businessData.agentReferenceNumber.get))
    else {
      Logger.warn(arnNotFoundError)
      Future.failed(new Exception(arnNotFoundError))
    }
  }

  private def agentEnrolmentAction(agentSubmissionModel: AgentSubmissionModel, reviewDetailsModel: ReviewDetails)
                                  (implicit hc: HeaderCarrier, request: Request[AnyContent]): Future[Result] = {
    agentService.getAgentEnrolmentResponse(agentSubmissionModel).flatMap {
      case SuccessAgentEnrolmentResponse =>
        Future.successful(Ok(views.html.confirmation.agentSubscriptionConfirmation(appConfig,
          reviewDetailsModel.agentReferenceNumber.get,
          LocalDate.now(),
          reviewDetailsModel.businessName)))
      case _ => Future.failed(new Exception(failedToEnrolError))
    }
  }

  val registeredAgent: Action[AnyContent] = authorisedActions.authorisedAgentAction { implicit user =>
    implicit request =>
      val result = {
        for {
          data <- handleBusinessData()
          submissionModel <- constructAgentSubmissionModel(data)
          result <- agentEnrolmentAction(submissionModel, data)
        } yield result
      }

      result.recoverWith {
        case error => Future.successful(InternalServerError)
      }
  }
}
