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

import common.Constants._
import config.AppConfig
import exceptions.AffinityGroupNotFoundException
import forms.OrganisationForm
import models.OrganisationModel
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class OrganisationTypeController @Inject()(appConfig: AppConfig,
                                           authorisationService: AuthorisationService,
                                           form: OrganisationForm,
                                           val messagesApi: MessagesApi)
  extends FrontendController with I18nSupport {

  private val incorrectAffinityGroupPage: String => Future[Result] = input => Future.successful(Redirect(
    controllers.routes.IncorrectAffinityGroupController.incorrectAffinityGroup(input)))

  val organisationType: Action[AnyContent] = Action.async { implicit request =>

    def routeRequest(affinityGroup: Option[String]): Future[Result] = affinityGroup match {
      case Some(AffinityGroup.Agent) => Future.successful(Redirect(
        controllers.routes.IncorrectAffinityGroupController.incorrectAffinityGroup(InvalidUserTypes.agent)))
      case Some(AffinityGroup.Organisation) => Future.successful(Ok(views.html.errors.organisationType(appConfig, form.organisationForm)))
      case _ => throw AffinityGroupNotFoundException("Affinity group not retrieved")
    }

    for {
      affinityGroup <- authorisationService.getAffinityGroup(hc)
      route <- routeRequest(affinityGroup)
    } yield route
  }

  val submitOrganisationType: Action[AnyContent] = Action.async { implicit request =>

    def errorAction(errors: Form[OrganisationModel]) = Future.successful(BadRequest(views.html.errors.organisationType(appConfig, errors)))

    def successAction(model: OrganisationModel) = model.organisationType match {
      case InvalidUserTypes.company => incorrectAffinityGroupPage(InvalidUserTypes.company)
      case InvalidUserTypes.charity => incorrectAffinityGroupPage(InvalidUserTypes.charity)
      case InvalidUserTypes.partnership => incorrectAffinityGroupPage(InvalidUserTypes.partnership)
      case InvalidUserTypes.trust => incorrectAffinityGroupPage(InvalidUserTypes.trust)
      case InvalidUserTypes.pensionTrust => incorrectAffinityGroupPage(InvalidUserTypes.pensionTrust)
    }

    form.organisationForm.bindFromRequest().fold(errorAction, successAction)
  }
}
