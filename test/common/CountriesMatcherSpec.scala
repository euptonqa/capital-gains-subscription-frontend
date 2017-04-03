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
