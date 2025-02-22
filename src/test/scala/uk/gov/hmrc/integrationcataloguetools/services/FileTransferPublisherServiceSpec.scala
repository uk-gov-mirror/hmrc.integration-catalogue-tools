package uk.gov.hmrc.integrationcataloguetools

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.io.Source
import io.swagger.v3.oas.models.OpenAPI

import uk.gov.hmrc.integrationcataloguetools.connectors._

import scala.collection.JavaConverters._
import org.mockito.Mock
import org.mockito.MockitoSugar
import org.mockito.ArgumentMatchersSugar
import java.nio.charset.StandardCharsets
import uk.gov.hmrc.integrationcataloguetools.models.Platform

class FileTransferPublisherServiceSpec extends AnyWordSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  val testResourcesPath = "src/test/resources/publishservicespec/"
  
  trait Setup {
    val mockPublisherConnector = mock[PublisherConnector]
    val service = new FileTransferPublisherService(mockPublisherConnector)
  }

  "Publish directory of files" should {
    val filename2 = "BMC-ServiceNow-NetworksDataDaily-notify.json"
    val filename3 = "BVD-DPS-PCPMonthly-pull.json"

    val directoryPath = testResourcesPath + "directory-of-ft-json-files"

    "be sucessfull if publish returns a 2xx" in new Setup {
      when(mockPublisherConnector.publishFileTransfer( (*)))
        .thenReturn(Right(Response(200, "")))

      val result = service.publishDirectory(directoryPath)

      result shouldBe Right()

      val expectedFtJsonContent1 = """{"publisherReference":"BMC-ServiceNow-NetworksDataDaily-notify","fileTransferSpecificationVersion":"0.1","title":"BMC-ServiceNow-NetworksDataDaily-notify","description":"A file transfer from BMC to Service Now","platformType":"CORE_IF_FILE_TRANSFER_FLOW","lastUpdated":"2020-11-04T20:27:05.000Z","contact":{"name":"EIS Front Door","emailAddress":"services.enterpriseintegration@hmrc.gov.uk"},"sourceSystem":["BMC"],"targetSystem":["ServiceNow"],"fileTransferPattern":"Corporate to corporate"}"""
      val expectedFtJsonContent2 = """{"publisherReference":"BVD-DPS-PCPMonthly-pull","fileTransferSpecificationVersion":"0.1","title":"BVD-DPS-PCPMonthly-pull","description":"A file transfer from Birth Verification Data (BVD) to Data Provisioning Systems (DPS)","platformType":"CORE_IF_FILE_TRANSFER_FLOW","lastUpdated":"2020-11-04T20:27:05.000Z","contact":{"name":"EIS Front Door","emailAddress":"services.enterpriseintegration@hmrc.gov.uk"},"sourceSystem":["BVD"],"targetSystem":["DPS"],"fileTransferPattern":"Corporate to corporate"}"""

      verify(mockPublisherConnector).publishFileTransfer(expectedFtJsonContent1)
      verify(mockPublisherConnector).publishFileTransfer(expectedFtJsonContent2)
    }

    "be unsucessfull if publish returns a non 2xx" in new Setup {
      when(mockPublisherConnector.publishFileTransfer((*)))
        .thenReturn(Right(Response(200, "")))
        .andThen(Right(Response(400, "Mock respose for invalid FT Json")))

      val result = service.publishDirectory(directoryPath)

      result shouldBe Left("Failed to publish 'BVD-DPS-PCPMonthly-pull.json'. Response(400): Mock respose for invalid FT Json")
    }

    "be unsucessfull if passed a file instead of a directory" in new Setup {
      
      val invalidDirectoryPath = directoryPath + filename2
      val result = service.publishDirectory(invalidDirectoryPath)

      result shouldBe Left(s"`$invalidDirectoryPath` is not a directory")
    }
}
}