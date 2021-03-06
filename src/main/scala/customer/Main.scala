package customer

import customer.domain.Customer
import customer.view.CustomerByEmailView
import customer.view.CustomerByNameView
import customer.view.CustomerEmailsByNameView
import kalix.scalasdk.Kalix
import org.slf4j.LoggerFactory
import customer.view.CustomerAddressesByNameView
import customer.view.CustomerAddressesByEmailView

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

object Main {

  private val log = LoggerFactory.getLogger("customer.Main")

  def createKalix(): Kalix = {
    // The KalixFactory automatically registers any generated Actions, Views or Entities,
    // and is kept up-to-date with any changes in your protobuf definitions.
    // If you prefer, you may remove this and manually register these components in a
    // `Kalix()` instance.
    KalixFactory.withComponents(
      new Customer(_),
      new CustomerAddressesByEmailView(_),
      new CustomerAddressesByNameView(_),
      new CustomerByEmailView(_),
      new CustomerByNameView(_),
      new CustomerEmailsByNameView(_))
  }

  def main(args: Array[String]): Unit = {
    log.info("starting the Kalix service")
    createKalix().start()
  }
}
