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
import com.google.inject.Inject
import config.AppConfig
import helpers.EnrolmentToCGTCheck
import models.SubscriptionReference
import play.api.mvc.{Action, AnyContent, Result}
import services.{AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                         appConfig: AppConfig,
                                                         subscriptionService: SubscriptionService,
                                                         authService: AuthorisationService)
  extends FrontendController {

  val residentIndividualSubscription: Action[AnyContent] = actions.authorisedResidentIndividualAction {
    implicit user =>
      implicit request =>
        for {
          enrolments <- authService.getEnrolments
          isEnrolled <- EnrolmentToCGTCheck.checkEnrolments(enrolments.get)
          redirect <- checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user, isEnrolled)
        } yield redirect
  }

  def checkForCgtRefAndRedirectToConfirmation(user: CgtIndividual): Future[Result] = {

    val nino = user.nino
    val cgtRef = subscriptionService.getSubscriptionResponse(nino.get)
    for {
      cgtRef <- cgtRef
      test <- matchCgtRef(cgtRef)
    } yield test

    def matchCgtRef(cgtReg: Option[SubscriptionReference]): Future[Result] = {
      for {
        cgtRef <- cgtRef
      }  yield cgtRef match {
        case Some(x) => Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(x.cgtRef))
        case _ => Redirect(controllers.routes.HelloWorld.helloWorld())
      }
    }
  }

  def checkForEnrolmentAndRedirectToConfirmationOrAlreadyEnrolled(user: CgtIndividual, isEnrolled: Boolean): Unit = {
    for {
      isEnrolled <- isEnrolled
    } yield isEnrolled match {
      case true => Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
      //you're already enrolled to CGT!
      case false => Future.successful(checkForCgtRefAndRedirectToConfirmation(user))
    }
  }
}
