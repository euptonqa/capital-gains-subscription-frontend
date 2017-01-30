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

import auth.AuthorisedActions
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import connectors.SubscriptionConnector
import helpers.EnrolmentToCGTCheck
import models.{Enrolment, SubscriptionReference}
import play.api.mvc._
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class NonResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                            appConfig: AppConfig,
                                                            cGTSubscriptionController: CGTSubscriptionController,
                                                            authorisationService: AuthorisationService,
                                                            subscriptionConnector: SubscriptionConnector)
  extends FrontendController {

  val nonResidentIndividualSubscription: Action[AnyContent] = actions.authorisedNonResidentIndividualAction {
    implicit user =>
      implicit request =>

        for {
          enrolments <- authorisationService.getEnrolments(hc(request))
          checkEnrolled <- checkEnrolments(enrolments)
          route <- routeRequest(checkEnrolled)
        } yield route
  }

  def checkEnrolments(enrolments: Option[Seq[Enrolment]]): Future[Boolean] = {
    EnrolmentToCGTCheck.checkEnrolments(enrolments)
  }

  def subscribeAndEnrollWithNino()(implicit request: Request[AnyContent]): Future[Result] = {

    def subscribeResultRoute(subscriptionRef: Option[SubscriptionReference]) = subscriptionRef match {
      case Some(data) => Future.successful(Redirect(routes.CGTSubscriptionController.confirmationOfSubscription(data.cgtRef)))
      case None => Future.successful(Redirect(Call("To 'We are experiencing technical difficulties' page", "")))
    }

    for {
      nino <- authorisationService.getNino(hc)
      enrol <- subscriptionConnector.getSubscriptionResponse(nino)(hc)
      route <- subscribeResultRoute(Some(SubscriptionReference("Subscription Reference")))
    } yield route
  }

  def captureAddress(): Future[Result] = {
    Future.successful(Redirect(Call("Redirect to the Address Details page", "")))
  }

  def notEnrolled()(implicit request: Request[AnyContent]): Future[Result] = {

    authorisationService.hasANino(hc).flatMap(
      i => {
        if (i) {
          subscribeAndEnrollWithNino()
        }
        else {
          Future.successful(Redirect(Call("Redirect to the Address Details page", "")))
        }
      }
    )
  }

  def routeRequest(alreadyEnrolled: Boolean)(implicit request: Request[AnyContent]): Future[Result] = {
    if (alreadyEnrolled) Future.successful(Redirect(Call("To the I-Form", "")))
    else notEnrolled()
  }
}
