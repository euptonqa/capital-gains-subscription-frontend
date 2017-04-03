package common

import java.util.Properties
import javax.inject.Inject

import play.api.Environment

import scala.collection.JavaConverters
import scala.io.Source

/**
  * Created by emma on 03/04/17.
  */
class CountriesMatcher @Inject()(environment: Environment) {
  lazy val p = new Properties
  p.load(Source.fromInputStream(environment.classLoader.getResourceAsStream("country-code.properties"), "UTF-8").bufferedReader())

  def getIsoCodeTupleList: List[(String, String)] = {
    JavaConverters.propertiesAsScalaMapConverter(p).asScala.toList.sortBy(_._2)
  }

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
