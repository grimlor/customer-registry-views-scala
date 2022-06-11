package customer.api

import customer.Main
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import customer.view.ByNameRequest
import customer.view.CustomerEmailsByName
import customer.view.EmailAddress
import akka.stream.scaladsl.Sink
import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.stream.Materializer
import customer.view.Addresses
import customer.domain.{Customer => DomainCustomer}
import org.scalatest.concurrent.Eventually
import customer.view.ByEmailRequest
import customer.view.CustomerAddressesByEmail

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class CustomerServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with Eventually
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()
  implicit private val mat = Materializer.matFromSystem(testKit.system)

  private val client = testKit.getGrpcClient(classOf[CustomerService])
  private val customerEmailsView = testKit.getGrpcClient(classOf[CustomerEmailsByName])
  private val customerAddressesView = testKit.getGrpcClient(classOf[CustomerAddressesByEmail])

  private val testCustomer = Customer(
        "abc123",
        "someone@example.com",
        "Someone",
        List(
          new Address(
            "123 Some Street",
            "Somewhere",
            "Of Mind",
            "11111",
            true
          ),
          new Address(
            "321 Elm Street",
            "Somewhere Else",
            "Of Decay",
            "99999",
            false
          )
        )
  )

  "CustomerService" must {

    "allow creation of a new Customer" in {
      val createResult = client.create(testCustomer)
      createResult.futureValue

      val getResult = client.getCustomer(GetCustomerRequest("abc123"))
      getResult.futureValue shouldBe testCustomer
    }

  }

  "CustomerEmailsByName" must {

    "return the emails for a given name" in {
      val viewSource: Source[EmailAddress,NotUsed] = customerEmailsView.getCustomerEmails(ByNameRequest("Someone"))
      
      val expected = EmailAddress(testCustomer.name, testCustomer.email)
      eventually {
        val result = viewSource.runWith(Sink.seq[EmailAddress]).futureValue
        result.length shouldBe 1
        result(0) shouldBe expected
      }
    }

  }

  "CustomerAddressesByEmail" must {

    "return the addresses in testCustomer" in {
      // val viewSource = customerAddressesView.getCustomerAddresses(ByEmailRequest("someone@example.com"))

      // val expected = Addresses(testCustomer.addresses.map(DomainCustomer.convertToDomain))
      // eventually {
      //   val result = viewSource.futureValue
      //   result shouldBe expected
      // }
    }

  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
