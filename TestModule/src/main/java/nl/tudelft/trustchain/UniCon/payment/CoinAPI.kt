package nl.tudelft.trustchain.TestModule.payment

abstract class CoinAPI {
    abstract fun addCoins()
    abstract fun subtractCoins()
    abstract fun checkBalance() : Float
    abstract fun sellContent()
    abstract fun buyContent()
}
