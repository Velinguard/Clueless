package clueless.api.config

import clueless.api.wallet.WalletWrapper
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.context.annotation.Configuration

@Configuration("config")
open class Config {
    lateinit var genesisFileLocation: String
    var walletConfig = WalletWrapper.WalletConfig()
    var walletCredentials = WalletWrapper.WalletCredentials()
    var poolConfig = PoolConfig()

    data class PoolConfig(
            @JsonProperty("genesis_txn") val genesis_txn: String =
                    "./docker/docker_pool_transactions_genesis"
    )
}