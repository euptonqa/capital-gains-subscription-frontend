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

import auth.AuthorisedActions
import com.google.inject.Inject
import config.AppConfig
import models.SubscriptionReference
import play.api.mvc.{Action, AnyContent}
import services.SubscriptionService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ResidentIndividualSubscriptionController @Inject()(actions: AuthorisedActions,
                                                         appConfig: AppConfig,
                                                         subscriptionService: SubscriptionService)
  extends FrontendController {

  val residentIndividualSubscription: Action[AnyContent] = actions.authorisedResidentIndividualAction {
    implicit user =>
      implicit request =>
        val nino = user.nino
        nino match {
          case Some(x) => {
            val cgtRef = subscriptionService.getSubscriptionResponse(x)
            for{
              cgtRef <- cgtRef
              test <- matchCgtRef(cgtRef)
            } yield test
        }
          case _ =>  Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
    }
  }

  def matchCgtRef(cgtRef: Option[SubscriptionReference]) = {
    cgtRef match {
      case Some(x) => Future.successful(Redirect(controllers.routes.CGTSubscriptionController.confirmationOfSubscription(x.cgtRef)))
      case _ => Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
    }
  }
}
