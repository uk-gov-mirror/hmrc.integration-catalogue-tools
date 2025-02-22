package uk.gov.hmrc.integrationcataloguetools

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.io.Source
import io.swagger.v3.oas.models.OpenAPI

import scala.collection.JavaConverters._
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.DefaultFormats
import uk.gov.hmrc.integrationcataloguetools.models.FileTransferPublishRequest

class GenerateFileTransferJsonSpec extends AnyWordSpec with Matchers {
  "Parse CSV into File Transfer Json list" in {
    val csvFile = Source.fromResource("generatefiletransferjsonspec/FileTransferDataCsv.csv")
    
    val fileTransfers : Seq[(_, FileTransferPublishRequest)] = GenerateFileTransferJson.fromCsvToFileTranferJson(csvFile.bufferedReader())

    fileTransfers should have length 1

    val (publisherReference, fileTransfer) = fileTransfers.head

    fileTransfer.contact.name shouldBe "EIS Front Door"
    fileTransfer.contact.emailAddress shouldBe "services.enterpriseintegration@hmrc.gov.uk"
    fileTransfer.description shouldBe "A file transfer from BMC to Service Now"
    fileTransfer.platformType shouldBe "CORE_IF_FILE_TRANSFER_FLOW"
    fileTransfer.title shouldBe "BMC-ServiceNow-NetworksDataDaily-notify"
    fileTransfer.publisherReference.value shouldBe "BMC-ServiceNow-NetworksDataDaily-notify"
    fileTransfer.fileTransferPattern shouldBe "Corporate to corporate"
    fileTransfer.sourceSystem.head shouldBe "BMC"
    fileTransfer.targetSystem.head shouldBe "ServiceNow"

  }
implicit val formats = DefaultFormats

  "Parse CSV into File Transfer Json content" in {
    val csvFile = Source.fromResource("generatefiletransferjsonspec/FileTransferDataCsv.csv")
    val expectedContent = Source.fromResource("generatefiletransferjsonspec/BMC-ServiceNow-NetworksDataDaily-notify.json").mkString

    val contents = GenerateFileTransferJson.fromCsvToFileTranferJson(csvFile.bufferedReader())

    contents should have length 1

    val content = write(contents.head._2)

    content shouldBe expectedContent
  }
}
