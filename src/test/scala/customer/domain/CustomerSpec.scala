package customer.domain

import com.google.protobuf.empty.Empty
import customer.api
import kalix.scalasdk.testkit.ValueEntityResult
import kalix.scalasdk.valueentity.ValueEntity
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CustomerSpec
    extends AnyWordSpec
    with Matchers {

  private val apiCustomer = api.Customer(
        "abc123",
        "someone@example.com",
        "Someone",
        List(
          new api.Address(
            "123 Some Street",
            "Somewhere",
            "Of Mind",
            "11111",
            true
          ),
          new api.Address(
            "321 Elm Street",
            "Somewhere Else",
            "Of Decay",
            "99999",
            false
          )
        )
      )

  "Customer" must {

    "handle command Create" in {
      val service = CustomerTestKit(new Customer(_))
      
      val result = service.create(apiCustomer)

      result.reply shouldBe Empty.defaultInstance
      service.currentState().addresses.length shouldBe 2
    }

    "handle command GetCustomer" in {
      val service = CustomerTestKit(new Customer(_))

      service.create(apiCustomer)

      val getResult = service.getCustomer(api.GetCustomerRequest("abc123"))
      getResult.reply shouldBe apiCustomer
    }

  }
}
