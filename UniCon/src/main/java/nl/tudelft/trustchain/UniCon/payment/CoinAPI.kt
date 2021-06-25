package nl.tudelft.trustchain.UniCon.payment

abstract class CoinAPI {
    abstract fun addCoins()
    abstract fun subtractCoins()
    abstract fun checkBalance() : Float
    abstract fun sellContent()
    abstract fun buyContent()
}
