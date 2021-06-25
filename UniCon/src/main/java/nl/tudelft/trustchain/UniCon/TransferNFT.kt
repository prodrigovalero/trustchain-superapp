package nl.tudelft.trustchain.UniCon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.trustchain.UniCon.payment.CoinAPI

class TransferNFT : AppCompatActivity() {
    val PATH = "nl.tudelft.trustchain.UniCon.payment."
    var CLASSNAME : String? = null
    val instance = IPv8Android.getInstance()
    val community = instance.getOverlay<TrustChainCommunity>()!!
    var coinClass : CoinAPI? = null
    val BLOCK_TYPE_TRANSFER_NFT = "transfer_nft"
    val BLOCK_TYPE_OPEN_TO_SELL_CONTENT = "sellable_content"
    val PRICE = "price"
    val CONTENT_HASH = "torrent_hash"
    val OWNERSHIP_CHAIN = "ownership_chain"
    val CONTENT_BLOCK = "content_block"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_nft)
        var btn: Button = findViewById(R.id.btnTransfer)
        btn.setOnClickListener(){
            transfer_nft()
        }
        val strObj = intent.getStringExtra("extra_class")
        CLASSNAME = intent.getStringExtra("extra_classname")
        var gson =  Gson();
        coinClass = gson.fromJson(strObj, Class.forName(PATH+CLASSNAME)) as CoinAPI?

    }

    private fun transfer_nft() {
        var blockHash: TextView = findViewById(R.id.blockHash)
        var hash = blockHash.text.toString()
        var info: TextView = findViewById(R.id.torrentView)
        nl.tudelft.trustchain.UniCon.transaction.transferNFT(
            hash,
            community,
            info,
            0.toFloat()
            //get_balance()
        )
    }

    private fun get_balance(): Float {
        return coinClass!!.checkBalance()
    }
}
