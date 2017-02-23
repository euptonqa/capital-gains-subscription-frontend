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
import config.AppConfig
import connectors.{KeystoreConnector, SuccessAgentEnrolmentResponse}
import models.{AgentSubmissionModel, ReviewDetails}
import java.time.LocalDate

import play.api.Logger
import services.AgentService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AgentController @Inject()(appConfig: AppConfig,
                                authorisedActions: AuthorisedActions,
                                agentService: AgentService,
                                sessionService: KeystoreConnector,
                                val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  private val businessDataNotFoundError: String = "Failed to retrieve registration details from BusinessCustomer keystore"
  private val arnNotFoundError: String = "Agent Details retrieved did not contain an ARN"
  private val failedToEnrolError: String = "Error returned from backend while attempting to enrol agent"

  val agent: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.setupYourAgency(appConfig)))
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
