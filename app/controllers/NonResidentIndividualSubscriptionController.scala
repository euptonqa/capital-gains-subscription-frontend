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

import auth.{AuthorisedActions, CgtIndividual}
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import connectors.SubscriptionConnector
import helpers.EnrolmentToCGTCheck
import play.api.mvc._
import services.{AuthorisationService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class NonResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                            appConfig: AppConfig,
                                                            subscriptionService: SubscriptionService,
                                                            authorisationService: AuthorisationService,
                                                            enrolmentToCGTCheck: EnrolmentToCGTCheck)
  extends FrontendController {

  val nonResidentIndividualSubscription: Action[AnyContent] = actions.authorisedNonResidentIndividualAction {
    implicit user =>
      implicit request =>
        for {
          enrolments <- authorisationService.getEnrolments(hc(request))
          checkEnrolled <- enrolmentToCGTCheck.checkEnrolments(enrolments)
          route <- routeRequest(checkEnrolled)
        } yield route
  }

  def routeRequest(alreadyEnrolled: Boolean)(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {
    //TODO: Update the route here to point to the actual Iform (on success)
    if (alreadyEnrolled) Future.successful(Redirect(routes.HelloWorld.helloWorld()))
    else notEnrolled()
  }

  def notEnrolled()(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {
    if (user.nino.isDefined) subscribeAndEnrollWithNino(user.nino.get)
    else Future.successful(Redirect(routes.UserDetailsController.userDetails()))
  }

  def subscribeAndEnrollWithNino(nino: String)(implicit request: Request[AnyContent], user: CgtIndividual): Future[Result] = {

    def subscribeResultRoute(subscriptionRef: Option[String]) = subscriptionRef match {
      case Some(data) => Future.successful(Redirect(routes.CGTSubscriptionController.confirmationOfSubscription(data)))
      case None => Future.successful(InternalServerError("DES responded with no subscription reference."))
    }

    for {
      enrol <- subscriptionService.getSubscriptionResponse(nino)(hc)
      route <- subscribeResultRoute(enrol)
    } yield route
  }
}
