package uk.gov.hmrc.integrationcataloguetools.connectors

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import uk.gov.hmrc.integrationcataloguetools.models.Platform

case class Response(statusCode: Int, content: String)

class PublisherConnector(url: String, client: CloseableHttpClient, platform: Platform, authorizationKey: String) {

  def publishApi(headers: Map[String, String], filename: String, oasContent: Array[Byte]): Either[String, Response] = {
    import org.apache.http.entity.ContentType
    import org.apache.http.entity.mime.MultipartEntityBuilder
    import org.apache.http.Header

    var put = new HttpPut(url)
    headers.foreach(header => put.addHeader(header._1, header._2))

    val entity = MultipartEntityBuilder.create()
    entity.addBinaryBody("selectedFile", oasContent, ContentType.DEFAULT_TEXT, filename)
    put.setEntity(entity.build());

    callEndpoint(put)

  }

  def publishFileTransfer(content: String) = {

    var put = new HttpPut(url)

    val entity: StringEntity = new StringEntity(content, ContentType.create("application/json", "UTF-8"));
    put.setEntity(entity);

    callEndpoint(put)
  }

  private def callEndpoint(put: HttpPut) = {
    
    put.addHeader("Authorization", authorizationKey)
    put.addHeader("x-platform-type", platform.value)

    Try(client.execute(put))
      .map((response: CloseableHttpResponse) => {
        val content = scala.io.Source.fromInputStream(response.getEntity().getContent()).mkString
        Response(response.getStatusLine().getStatusCode(), content)
      }) match {
      case Success(response)  => Right(response)
      case Failure(exception) => {
        println("Error calling publish service:")
        exception.printStackTrace()
        Left(exception.getMessage())
      }
    }
  }

}
