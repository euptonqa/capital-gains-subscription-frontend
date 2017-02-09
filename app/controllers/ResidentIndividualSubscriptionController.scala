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

import javax.inject.Singleton

import auth.{AuthorisedActions, CgtIndividual}
import javax.inject.Inject

import config.AppConfig
import helpers.EnrolmentToCGTCheck
import models.SubscriptionReference
import play.api.mvc.{Action, AnyContent, Result}
import services.{AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                         appConfig: AppConfig,
                                                         subscriptionService: SubscriptionService,
                                                         authService: AuthorisationService,
                                                         enrolmentToCGTCheck: EnrolmentToCGTCheck)
  extends FrontendController {

  val residentIndividualSubscription: Action[AnyContent] =
    actions.authorisedResidentIndividualAction {
    implicit user =>
      implicit request =>
        for {
          enrolments <- authService.getEnrolments
          isEnrolled <- enrolmentToCGTCheck.checkEnrolments(enrolments)
          redirect <- checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user, isEnrolled)
        } yield redirect
  }


  def checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user: CgtIndividual, isEnrolled: Boolean)(implicit hc: HeaderCarrier): Future[Result] = {
    if (isEnrolled) Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
    //TODO: you're already enrolled to CGT!
    else checkForCgtRefAndRedirectToConfirmation(user)
  }

  def checkForCgtRefAndRedirectToConfirmation(user: CgtIndividual)(implicit hc: HeaderCarrier): Future[Result] = {

    val nino = user.nino
    val cgtRefNumber = subscriptionService.getSubscriptionResponse(nino.get)

    def redirectToCGTConfirmationOrError(cgtRef: Option[SubscriptionReference]): Future[Result] = {
      cgtRef match {
        case Some(x) => Future.successful(Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(x.cgtRef)))
        case _ => Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
      }
    }

    for {
      cgtRef <- cgtRefNumber
      test <- redirectToCGTConfirmationOrError(cgtRef)
    } yield test
  }
}
