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

package predicates

import java.net.URI

import play.api.mvc.Results._
import play.api.mvc.{Request, AnyContent}
import uk.gov.hmrc.play.frontend.auth._
import helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TwoFAPredicate(errorPageUri: URI) extends PageVisibilityPredicate {

  private val errorPage = Future.successful(Redirect(errorPageUri.toString))

  override def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] = {

    val strong = StrongCredentialCheck.checkCredential(authContext)

    for {
      strongCred <- strong
    } yield strongCred match {
      case true => PageIsVisible
      case _ => PageBlocked(errorPage)
    }
  }
}
