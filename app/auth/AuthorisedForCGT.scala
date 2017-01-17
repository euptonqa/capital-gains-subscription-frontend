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

import com.google.inject.Inject
import config.ApplicationConfig
import connectors.FrontendAuthorisationConnector
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.AuthorisationService
import predicates.CompositePredicate
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts
import uk.gov.hmrc.play.frontend.auth.{Actions, AuthContext, AuthenticationProvider, TaxRegime}

import scala.concurrent.Future

class AuthorisedForCGT @Inject() (applicationConfig: ApplicationConfig, authorisationService: AuthorisationService,
                                  frontendAuthorisationConnector: FrontendAuthorisationConnector) extends Actions {


  private type AsyncPlayRequest = Request[AnyContent] => Future[Result]
  private type AsyncUserRequest = CGTUser => AsyncPlayRequest

  val authConnector = frontendAuthorisationConnector
  lazy val postSignInRedirectUrl: String = applicationConfig.individualResident

  lazy val visibilityPredicate = new CompositePredicate(applicationConfig,
    authorisationService)(applicationConfig.individualResident, applicationConfig.notAuthorisedRedirectUrl,
    applicationConfig.ivUpliftUrl, applicationConfig.twoFactorUrl, "", "")

  class AuthorisedBy(regime: TaxRegime) {
    lazy val authenticatedBy: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    def async(action: AsyncUserRequest): Action[AnyContent] = {

      authenticatedBy.async {
        authContext: AuthContext => implicit request =>
          action(CGTUser(authContext))(request)
      }
    }
  }

  val authorised = new AuthorisedBy(CgtAnyRegime)

  lazy val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.governmentGateway)

  trait CgtRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true

    override def authenticationType: AuthenticationProvider = ggProvider

    override def unauthorisedLandingPage: Option[String] = Some(applicationConfig.notAuthorisedRedirectUrl)
  }

  object CgtAnyRegime extends CgtRegime

}
