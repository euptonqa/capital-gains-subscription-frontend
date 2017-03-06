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

package auth

import javax.inject.Inject

import config.ApplicationConfig
import connectors.FrontendAuthorisationConnector
import play.api.mvc.{Action, AnyContent}
import predicates._
import services.AuthorisationService
import types.{AuthenticatedAgentAction, AuthenticatedIndividualAction, AuthenticatedNROrganisationAction}
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts
import uk.gov.hmrc.play.frontend.auth.{Actions, AuthContext, AuthenticationProvider, TaxRegime}

class AuthorisedActions @Inject()(applicationConfig: ApplicationConfig,
                                  authorisationService: AuthorisationService,
                                  frontendAuthorisationConnector: FrontendAuthorisationConnector)
  extends Actions {

  override val authConnector: FrontendAuthorisationConnector = frontendAuthorisationConnector

  private val composeAuthorisedAgentAction: AuthenticatedAgentAction => Action[AnyContent] = {
    val postSignInRedirectUrl = applicationConfig.agentPostSignIn
    val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.governmentGateway)
    val regime = new CgtRegime {
      override def authenticationType: AuthenticationProvider = ggProvider
    }

    lazy val visibilityPredicate = new AgentVisibilityPredicate(applicationConfig,
      authorisationService)(applicationConfig.agentBadAffinity)

    lazy val guardedAction: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    val authenticatedAction: AuthenticatedAgentAction => Action[AnyContent] = action => {
      guardedAction.async {
        authContext: AuthContext =>
          implicit request =>
            action(CgtAgent(authContext))(request)
      }
    }

    authenticatedAction
  }

  private val composeAuthorisedResidentIndividualAction: AuthenticatedIndividualAction => Action[AnyContent] = {

    val postSignInRedirectUrl: String = applicationConfig.individualResident
    val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.governmentGateway)
    val regime = new CgtRegime {
      override def authenticationType: AuthenticationProvider = ggProvider
    }

    lazy val visibilityPredicate = new ResidentIndividualVisibilityPredicate(
      applicationConfig,
      authorisationService)(postSignInRedirectUrl,
      applicationConfig.notAuthorisedRedirectUrl,
      applicationConfig.ivUpliftUrl,
      applicationConfig.twoFactorUrl,
      applicationConfig.individualBadAffinity,
      "")

    lazy val guardedAction: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    val authenticatedAction: AuthenticatedIndividualAction => Action[AnyContent] = action => {

      guardedAction.async {
        authContext: AuthContext =>
          implicit request =>
            action(CgtIndividual(authContext))(request)
      }
    }

    authenticatedAction
  }

  private val composeAuthorisedNonResidentIndividualAction: AuthenticatedIndividualAction => Action[AnyContent] = {
    val postSignInRedirectUrl: String = applicationConfig.individualNonResident
    val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.governmentGateway)
    val regime = new CgtRegime {
      override def authenticationType: AuthenticationProvider = ggProvider
    }

    lazy val visibilityPredicate = new NonResidentIndividualVisibilityPredicate(
      applicationConfig,
      authorisationService)(postSignInRedirectUrl,
      applicationConfig.notAuthorisedRedirectUrl,
      applicationConfig.twoFactorUrl,
      applicationConfig.individualBadAffinity)

    lazy val guardedAction: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    val authenticatedAction: AuthenticatedIndividualAction => Action[AnyContent] = action => {

      guardedAction.async {
        authContext: AuthContext =>
          implicit request =>
            action(CgtIndividual(authContext))(request)
      }
    }

    authenticatedAction
  }

  private val composeAuthorisedNonResidentOrganisationAction: AuthenticatedNROrganisationAction => Action[AnyContent] = {
    val postSignInRedirectUrl: String = controllers.routes.CompanyController.subscribe().url
    val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.governmentGateway)

    val regime = new CgtRegime {
      override def authenticationType: AuthenticationProvider = ggProvider
    }

    lazy val visibilityPredicate = new NonResidentOrganisationVisibilityPredicate(
      authorisationService)("http://www.gov.uk" //TODO: add url for error page
    )

    lazy val guardedAction: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    val authenticatedAction: AuthenticatedNROrganisationAction => Action[AnyContent] = action => {

      guardedAction.async {
        authContext: AuthContext =>
          implicit request =>
            action(CgtNROrganisation(authContext))(request)
      }
    }
    authenticatedAction
  }

  def authorisedResidentIndividualAction(action: AuthenticatedIndividualAction): Action[AnyContent] = composeAuthorisedResidentIndividualAction(action)

  def authorisedNonResidentIndividualAction(action: AuthenticatedIndividualAction): Action[AnyContent] = composeAuthorisedNonResidentIndividualAction(action)

  def authorisedNonResidentOrganisationAction(action: AuthenticatedNROrganisationAction): Action[AnyContent] =
    composeAuthorisedNonResidentOrganisationAction(action)

  def authorisedAgentAction(action: AuthenticatedAgentAction): Action[AnyContent] = composeAuthorisedAgentAction(action)

  trait CgtRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true

    override def authenticationType: AuthenticationProvider

    override def unauthorisedLandingPage: Option[String] = Some(applicationConfig.notAuthorisedRedirectUrl)
  }

}
