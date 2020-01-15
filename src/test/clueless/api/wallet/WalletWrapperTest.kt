package clueless.api.wallet

import clueless.api.hyperledger.HyperledgerWallet
import org.hyperledger.indy.sdk.wallet.Wallet
import org.testng.annotations.Test;
import org.mockito.Mockito.*

class WalletWrapperTest {
    private val wallet: HyperledgerWallet = mock(HyperledgerWallet::class.java)
    private val api: WalletWrapper = WalletWrapper(wallet)
    private var walletMockito: Wallet = mock(Wallet::class.java)

    @Test
    fun `createWallet() correctly delegates`() {
        val id = "test"
        val key = "test-key"

        api.createWallet(id, key)
        verify(
                wallet,
                times(1)
        ).createWallet(WalletWrapper.WalletConfig(id), WalletWrapper.WalletCredentials(key))
    }

    @Test
    fun `getWallet() opens a new wallet`() {
        val id = "test"
        val key = "test-key"

        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id), WalletWrapper.WalletCredentials(key))
        ).thenReturn(walletMockito)

        api.getWallet(id, key)

        verify(
                wallet,
                times(1)
        ).openWallet(WalletWrapper.WalletConfig(id), WalletWrapper.WalletCredentials(key))
    }

    @Test
    fun `deleteWallet() correctly delegates`() {
        val id = "test"
        val key = "test-key"

        api.deleteWallet(id, key)
        verify(
                wallet,
                times(1)
        ).deleteWallet(WalletWrapper.WalletConfig(id), WalletWrapper.WalletCredentials(key))
    }
}