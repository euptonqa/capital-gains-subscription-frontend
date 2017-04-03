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

package common

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Environment

/**
  * Created by emma on 03/04/17.
  */
class CountriesMatcherSpec extends PlaySpec with OneServerPerSuite {
  val countriesHelper = new CountriesMatcher(Environment.simple())

  "countriesHelper" must {

    "getSelectedCountry" must {
      "bring the correct country from the file" in {
        countriesHelper.getSelectedCountry("GB") must be("United Kingdom")
        countriesHelper.getSelectedCountry("US") must be("USA")
        countriesHelper.getSelectedCountry("VG") must be("British Virgin Islands")
        countriesHelper.getSelectedCountry("UG") must be("Uganda")
        countriesHelper.getSelectedCountry("zz") must be("zz")
      }
    }

    "getIsoCodeMap" must {
      "return map of country iso-code to country name" in {
        countriesHelper.getIsoCodeTupleList must contain(("US" , "USA :United States of America"))
        countriesHelper.getIsoCodeTupleList must contain(("GB" , "United Kingdom :UK, GB, Great Britain"))
        countriesHelper.getIsoCodeTupleList must contain(("UG" , "Uganda"))
      }
    }
  }
}
