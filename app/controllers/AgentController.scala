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
import common.Constants.ErrorMessages._
import config.AppConfig
import connectors.{KeystoreConnector, SuccessAgentEnrolmentResponse}
import helpers.{EnrolmentToCGTCheck, LogicHelpers}
import models.{AgentSubmissionModel, ReviewDetails}
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
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
                                subscriptionService: SubscriptionService,
                                val messagesApi: MessagesApi,
                                logicHelpers: LogicHelpers) extends FrontendController with I18nSupport {

  val agent: String => Action[AnyContent] = url => authorisedActions.authorisedAgentAction(Some(url)) {
    implicit user =>
      implicit request =>

        val saveUrl = logicHelpers.saveCallbackUrl(url)

        def checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user: CgtAgent,
                                                                        isEnrolled: Boolean)(implicit hc: HeaderCarrier): Future[Result] = {
          if (isEnrolled) Future.successful(Redirect(url))
          else Future.successful(Ok(views.html.setupYourAgency(appConfig)))
        }

        for {
          save <- saveUrl
          enrolments <- authService.getEnrolments
          isEnrolled <- EnrolmentToCGTCheck.checkAgentEnrolments(enrolments)
          redirect <- checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user, isEnrolled)
        } yield redirect
  }

  private[controllers] def handleBusinessData()(implicit hc: HeaderCarrier): Future[ReviewDetails] = {
    sessionService.fetchAndGetBusinessData().flatMap {
      case Some(details) => Future.successful(details)
      case None => Logger.warn(businessDataNotFound)
        Future.failed(new Exception(businessDataNotFound))
    }
  }

  private[controllers] def constructAgentSubmissionModel(businessData: ReviewDetails): Future[AgentSubmissionModel] = {
    if (businessData.agentReferenceNumber.isDefined) Future.successful(AgentSubmissionModel(businessData.safeId, businessData.agentReferenceNumber.get))
    else {
      Logger.warn(arnNotFound)
      Future.failed(new Exception(arnNotFound))
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
      case _ => Future.failed(new Exception(failedToEnrolAgent))
    }
  }

  val registeredAgent: Action[AnyContent] = authorisedActions.authorisedAgentAction() { implicit user =>
    implicit request =>
      val result = {
        for {
          data <- handleBusinessData()
          submissionModel <- constructAgentSubmissionModel(data)
          result <- agentEnrolmentAction(submissionModel, data)
        } yield result
      }

      result.recoverWith {
        case error => Future.failed(error)
      }
  }
}
