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

import java.util.Properties
import javax.inject.Inject

import play.api.Environment

import scala.collection.JavaConverters
import scala.io.Source

class CountriesMatcher @Inject()(environment: Environment) {
  lazy val p = new Properties
  // format of file is CODE=Country Name e.g. AE=United Arab Emirates
  p.load(Source.fromInputStream(environment.classLoader.getResourceAsStream("country-code.properties"), "UTF-8").bufferedReader())

  val countries: Map[String, String] = JavaConverters.propertiesAsScalaMapConverter(p).asScala.toMap

  def getCountryCode(countryName: String): Option[String] = countries.collectFirst {
    case (code, name) if name.toLowerCase == countryName.toLowerCase => code
  }

  def getCountryName(countryCode: String): Option[String] = countries.collectFirst {
    case (code, name) if code == countryCode => name
  }

  // ^ Mac's functions for getting the countryName/countryCode

  def getIsoCodeTupleList: List[(String, String)] = countries.toList.sortBy(_._2)

  //def getCountryCode(countryString: String): Option[String] = countries.find(kv => kv._2 == countryString).map(_._1)
  //    JavaConverters.propertiesAsScalaMapConverter(p).asScala.toList.filter(_._2.contains(countryString)).map(_._1).head


  //^Contains CountriesMatcher code
  def getSelectedCountry(isoCode: String): String = {
    def trimCountry(selectedCountry: String) = {
      val position = selectedCountry.indexOf(":")
      if (position > 0) {
        selectedCountry.substring(0, position).trim
      } else {
        selectedCountry
      }
    }

    def getCountry(isoCode: String): Option[String] = {
      val country = Option(p.getProperty(isoCode.toUpperCase))
      country.map{ selectedCountry =>
        trimCountry(selectedCountry)
      }
    }

    getCountry(isoCode.toUpperCase).fold(isoCode){x=>x}
  }

}
