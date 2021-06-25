package nl.tudelft.trustchain.UniCon.payment

class Ethereum : CoinAPI() {
    private var balance : Float = 0.0f

    override fun addCoins() {
        throw Exception("Exception raised at add_coins method in ethereum implementation")
    }

    override fun subtractCoins() {
        throw Exception("Exception raised at subtract_coins method in ethereum implementation")
    }

    override fun checkBalance() : Float {
        TODO("Not yet implemented")
    }

    override fun sellContent() {
        TODO("Not yet implemented")
    }

    override fun buyContent() {
        TODO("Not yet implemented")
    }

}
