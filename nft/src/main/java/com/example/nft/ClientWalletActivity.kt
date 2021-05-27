package com.example.nft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ClientWalletActivity : AppCompatActivity(){
    val PATH = "com.example.nft."
    val CLASSNAME = "Ethereum"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_wallet)
        val create_NFT_button: Button = findViewById<View>(R.id.create_NFT) as Button
        create_NFT_button.setOnClickListener(){
            createNFT()
        }

        val coinClass : Class<out com.example.nft.CoinAPI> //TODO check this
        coinClass = Class.forName(PATH+CLASSNAME) as Class<out CoinAPI>
        val coinImplementation = coinClass.getConstructor().newInstance()


        val add_coins_button: Button = findViewById<View>(R.id.add_coins) as Button

        add_coins_button.setOnClickListener(){
            coinImplementation.addCoins()
        }


        val subtract_coins_button: Button = findViewById<View>(R.id.subtract_coins) as Button

        subtract_coins_button.setOnClickListener(){
            coinImplementation.subtractCoins()
        }

    }
    private fun createNFT(){

        val intent = Intent(this@ClientWalletActivity, Create_NFT::class.java)
        startActivity(intent)
    }

}
