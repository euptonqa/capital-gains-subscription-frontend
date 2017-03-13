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

import auth.{AuthorisedActions, CgtNROrganisation}
import config.AppConfig
import helpers.EnrolmentToCGTCheck
import play.api.mvc.{Action, AnyContent, Result}
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CompanyController @Inject()(appConfig: AppConfig,
                                  authorisedActions: AuthorisedActions,
                                  authService: AuthorisationService) extends FrontendController {

  val businessCustomerFrontendUrl: String = appConfig.businessCompanyFrontendRegister

  val subscribe: Action[AnyContent] = authorisedActions.authorisedNonResidentOrganisationAction {
    implicit user =>
      implicit request =>

        def checkForEnrolmentsAndRedirect(user: CgtNROrganisation, isEnrolled: Boolean)(): Future[Result] ={
          if (isEnrolled) Future.successful(Redirect(appConfig.iFormUrl))
          else Future.successful(Redirect(businessCustomerFrontendUrl))
        }

        for {
          enrolments <- authService.getEnrolments
          isEnrolled <- EnrolmentToCGTCheck.checkEnrolments(enrolments)
          redirect <- checkForEnrolmentsAndRedirect(user, isEnrolled)
        } yield redirect

  }
}