package com.example.nft

class Ethereum : CoinAPI() {
    override fun addCoins() {
        throw Exception("Exception raised at add_coins method in ethereum implementation")
    }

    override fun subtractCoins() {
        throw Exception("Exception raised at subtract_coins method in ethereum implementation")
    }

}
