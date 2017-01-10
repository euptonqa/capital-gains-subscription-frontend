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

import java.net.URI

import play.api.mvc.{Action, AnyContent, Request, Result}
import predicates.LoginPredicate
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts

import scala.concurrent.Future

trait AuthorisedForCGT extends Actions {

  class AuthorisedBy(regime: TaxRegime) {
    val authedBy: AuthenticatedBy = AuthorisedFor(regime, new CompositePageVisibilityPredicate {
      override def children: Seq[PageVisibilityPredicate] = Seq(new LoginPredicate(new URI("")))
    })

    def async(action: Request[AnyContent] => Future[Result]): Action[AnyContent] = {
      authedBy.async {
        authContext: AuthContext => implicit request =>
          action(request)
      }
    }
  }

  val ggProvider = new GovernmentGatewayProvider("test", "test")

  trait CGTRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true
    override def authenticationType: AuthenticationProvider = ggProvider
  }

  object CGTAnyRegime extends CGTRegime
}
