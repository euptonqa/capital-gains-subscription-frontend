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

import auth.AuthorisedForCGT
import com.google.inject.Inject
import config.ApplicationConfig
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ResidentIndividualSubscriptionController @Inject()(authService: AuthorisationService,
                                                         appConfig: ApplicationConfig)
  extends FrontendController with AuthorisedForCGT {

  lazy val authorisationService = authService
  lazy val applicationConfig = appConfig

  val residentIndividualSubscription = Authorised.async { implicit user => implicit request =>
    Future.successful(Redirect(controllers.routes.HelloWorld.helloWorld()))
  }
}
