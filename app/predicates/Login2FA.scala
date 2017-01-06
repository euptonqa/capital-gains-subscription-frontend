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

import play.api.mvc.Request
import play.mvc.BodyParser.AnyContent
import uk.gov.hmrc.play.frontend.auth.{AuthContext, PageVisibilityPredicate, PageVisibilityResult}

import scala.concurrent.Future

class Login2FA extends PageVisibilityPredicate {
  override def apply(authContext: AuthContext, request:Request[AnyContent]): Future[PageVisibilityResult] =
    Future.successful(authContext.)
}
