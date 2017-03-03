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

package config

import javax.inject.{Inject, Singleton}

import play.api.{Application, Configuration}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val assetsPrefix: String
  val analyticsToken: String
  val analyticsHost: String
  val contactFormServiceIdentifier: String
  val contactFrontendPartialBaseUrl: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val identityVerification: Boolean
  val ivUpliftUrl: String
  val notAuthorisedRedirectUrl: String
  val twoFactorUrl: String
  val governmentGateway: String
  val individualResident: String
  val individualNonResident: String
  val individualBadAffinity: String
  val agentBadAffinity: String
  val agentPostSignIn: String
  val subscription: String
  val businessCompanyFrontendRegister: String
  val businessCompanyFrontendAgentRegister: String
  val agentServiceUrl: String
  val iFormUrl: String
}

@Singleton
class ApplicationConfig @Inject()(configuration: Configuration, val app: Application) extends AppConfig {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))
  private def constructUrl(key: String) = baseUrl(key) + configuration.getString(s"microservice.services.$key.path").getOrElse("")

  private lazy val contactFrontendService = baseUrl("contact-frontend")
  private lazy val contactHost = configuration.getString(s"$env.microservice.services.contact-frontend.host").getOrElse("")
  override val contactFormServiceIdentifier = "CGT-Subscription"

  override lazy val assetsPrefix: String = loadConfig(s"assets.url") + loadConfig(s"assets.version")
  override lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  override lazy val contactFrontendPartialBaseUrl = s"$contactFrontendService"
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override val identityVerification: Boolean = configuration.getBoolean("microservice.services.features.identityVerification").getOrElse(false)
  override val ivUpliftUrl: String = configuration.getString(s"identity-verification-uplift.host").getOrElse("")
  override lazy val notAuthorisedRedirectUrl: String = configuration.getString("not-authorised-callback.url").getOrElse("")
  override lazy val twoFactorUrl: String = configuration.getString(s"two-factor.host").getOrElse("")
  override lazy val governmentGateway: String = configuration.getString(s"government-gateway-sign-in.host").getOrElse("")
  override lazy val individualResident: String = configuration.getString(s"resident-individual-sign-in.url").getOrElse("")
  override lazy val individualNonResident: String = configuration.getString(s"non-resident-individual-sign-in.url").getOrElse("")
  override lazy val individualBadAffinity: String = configuration.getString(s"resident-individual-bad-affinity.url").getOrElse("")
  override lazy val agentBadAffinity: String = configuration.getString(s"agent-bad-affinity.url").getOrElse("")
  override lazy val agentPostSignIn: String = configuration.getString(s"agent-post-siginin.url").getOrElse("")
  override lazy val subscription: String = configuration.getString(s"subscription.url").getOrElse("")
  override lazy val businessCompanyFrontendRegister: String = constructUrl("business-customer")
  override lazy val businessCompanyFrontendAgentRegister: String = constructUrl("business-customer-agents")
  override lazy val agentServiceUrl: String = constructUrl("agent-service-frontend")
  override lazy val iFormUrl: String = configuration.getString(s"iform-url").getOrElse("")
}
