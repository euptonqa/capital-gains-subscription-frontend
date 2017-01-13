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

import java.util

import com.google.inject.{Inject, Singleton}
import uk.gov.service.notify._

@Singleton
class MessagesTest @Inject()() {

   def testTextMessage(): Unit = {
     val javaMap: util.Map[String, String] =  new util.HashMap[String, String]()

     javaMap.put("name", "A Name of Monstrous Proportions")
     javaMap.put("reference", "XC1238321248213")

//    val map = Map("name" -> "A Name of Monstrous Proportions", "reference" -> "XC1238321248213").asInstanceOf[util.Map[String, String]]
    val client = new NotificationClient("testing_dev_api_key-997a7f44-6d8f-4a35-9888-f728ee57d9b1-8db2ddbc-3e02-4ebb-b1c7-b455cfdd2389")
    client.sendSms("9b7e6622-dc10-4aad-9a61-2b005cddb20a", "+447867676676", javaMap, "REF-TEST-QQ")
  }
}
