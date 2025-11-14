package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "adjust_balance_success"

    request {
        method POST()
        urlPath("/api/accounts/adjust") {
            queryParameters {
                parameter("login", "user1")
                parameter("amount", "100.00")
            }
        }
    }

    response {
        status OK()
    }
}

