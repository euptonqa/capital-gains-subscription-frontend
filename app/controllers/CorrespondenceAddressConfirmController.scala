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

import javax.inject.Inject

import config.{AppConfig, SubscriptionSessionCache}
import connectors.KeystoreConnector
import models.{CompanyAddressModel, YesNoModel}
import play.api.data.Form
import play.api.mvc.{Action, Result}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.frontend.controller.FrontendController
import forms.YesNoForm

import scala.concurrent.Future

@Singleton
class CorrespondenceAddressConfirmController @Inject()(appConfig: AppConfig,
                                                       subscriptionSessionCache: SubscriptionSessionCache,
                                                       servicesConfig: ServicesConfig,
                                                       yesNoForm: YesNoForm) extends FrontendController {


  val keyStoreConnector = new KeystoreConnector(appConfig, subscriptionSessionCache, servicesConfig)

  val keystoreKey = "stubKey"
  //TODO: Find out actual key/create key

  val correspondenceAddressConfirm = Action.async {
    implicit request =>
      val containsData = Ok(views.html.helloworld.hello_world(appConfig))
      val doesNotContainData = Ok(views.html.helloworld.hello_world(appConfig))
      //TODO: Replace with correspondence address and fill for containsData with obtained KS data
      keyStoreConnector.fetchAndGetFormData[CompanyAddressModel](keystoreKey).map {
        case Some(data) => containsData
        case None => doesNotContainData
      }
  }

  val submitCorrespondenceAddressConfirm = Action.async { implicit request =>
    val yes = Redirect(routes.HelloWorld.helloWorld())
    //TODO: Capital Gains Contact Details page
    val no = Redirect(routes.HelloWorld.helloWorld())
    //TODO: User details/enter correspondence details page

    def errorAction(form: Form[YesNoModel]): Future[Result] ={
      keyStoreConnector.fetchAndGetFormData[CompanyAddressModel](keystoreKey).map {
        case Some(data) => BadRequest(views.html.useRegisteredAddress(appConfig, form, data))
        case None => BadRequest(views.html.useRegisteredAddress(appConfig, form,
          CompanyAddressModel(None, None, None, None, None, None)))
      }
    }

    def successAction(model: YesNoModel) = {
      for {
        save <- keyStoreConnector.saveFormData[YesNoModel](keystoreKey, model)
        route <- routeRequest(model)
      } yield route
    }

    def routeRequest(data: YesNoModel): Future[Result] = {
      if(data.response)
        Future.successful(Redirect(routes.HelloWorld.helloWorld()))
      else
        Future.successful(no)
    }

    yesNoForm.yesNoForm.bindFromRequest.fold(errorAction, successAction)
  }

}