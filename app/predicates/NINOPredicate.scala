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

import helpers.NINOCheck
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.play.frontend.auth._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class NINOPredicate(ninoURI: URI) extends PageVisibilityPredicate {
  override def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] = {
    NINOCheck.checkNINO(authContext).map {
      case true => PageIsVisible
      case _ => PageBlocked(needsNINO)
    }
  }
  private val needsNINO = Future.successful(Redirect(ninoURI.toString))
}
