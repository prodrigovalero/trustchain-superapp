package nl.tudelft.trustchain.UniCon.transaction

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.frostwire.jlibtorrent.Sha1Hash
import nl.tudelft.ipv8.attestation.trustchain.ANY_COUNTERPARTY_PK
import nl.tudelft.ipv8.attestation.trustchain.EMPTY_PK
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity

val BLOCK_TYPE_CREATE_CONTENT = "create_content"
val BLOCK_TYPE_OPEN_TO_SELL_CONTENT = "sellable_content"
val CONTENT_HASH = "content_hash"
val OWNERSHIP_CHAIN = "ownership_chain"
val PRICE = "price"
val CONTENT_BLOCK = "content_block"
val BLOCK_TYPE_TRANSFER_NFT = "transfer_nft"

@RequiresApi(Build.VERSION_CODES.N)
fun createNFT(hash: Sha1Hash, price: Float, previousBlockHash: String, box : Boolean, community : TrustChainCommunity){

    var block : TrustChainBlock?
    if (previousBlockHash == ""){

        block = newNFT(hash, price, community)

    }
    else{
        block = buildUponNFT(hash, previousBlockHash.toByteArray(), price, community)
    }
    if (box){
        Log.i("personal", "MINE: before open to sell transaction")
        var blockhash = block.calculateHash()
        Log.i("personal", "MINE: block hash is:" + blockhash)
        val transaction = mapOf(
            CONTENT_HASH to block.transaction.get(CONTENT_HASH),
            OWNERSHIP_CHAIN to block.transaction.get(OWNERSHIP_CHAIN),
            PRICE to block.transaction.get(PRICE),
            CONTENT_BLOCK to blockhash.toString())

        community.createProposalBlock(
            BLOCK_TYPE_OPEN_TO_SELL_CONTENT,
            transaction,
            ANY_COUNTERPARTY_PK
        )
        Log.i("personal", "MINE: after block created")

    }

}
fun newNFT(torrentHash: Sha1Hash, price : Float, community: TrustChainCommunity): TrustChainBlock {
    //Log.i("personal", "MINE: In newNFT function, the type of torrentHash.toString is: " + torrentHash.toString().javaClass)

    var previousOwners : MutableList<String> = ArrayList()
    previousOwners.add(0, community.myPeer.publicKey.toString())
    var nftHash = torrentHash.toString()
    //Log.i("personal", "The type of the transaction value 1 is: " + nftHash.javaClass + " with value:  " + nftHash)
    //Log.i("personal", "The type of the transaction value 2 is: " + previousOwners.javaClass + " with value " + previousOwners)
    //Log.i("personal", "The type of the transaction value 3 is: " + price.javaClass + "with value: " + price)
    val transaction = mapOf(
        CONTENT_HASH to nftHash,
        OWNERSHIP_CHAIN to previousOwners,
        PRICE to price
        //TransactionRepository.KEY_AMOUNT to BigInteger.valueOf(amount),
        //TransactionRepository.KEY_BALANCE to (BigInteger.valueOf(getMyBalance() - amount).toLong())
    )
    var block = community.createProposalBlock(
        BLOCK_TYPE_CREATE_CONTENT, transaction,
        ANY_COUNTERPARTY_PK
    )

    //Log.i("personal", "MINE: THE NFT IS CREATED JODEEEEER!!!" + block.calculateHash())
    //var torrentInfo = findViewById<TextView>(R.id.torrentView)
    //torrentInfo.setText("CONTENT IS CREATED!!")
    return block
}
@Suppress("UNCHECKED_CAST")
fun buildUponNFT(torrentHash : Sha1Hash, previousBlockHash : ByteArray, price : Float, community: TrustChainCommunity) : TrustChainBlock {
    Log.i("personal", "MINE: In buildUponNFT function")

    var previousBlock = community.database.getBlockWithHash(previousBlockHash)

    var previousOwners = previousBlock!!.transaction.get(OWNERSHIP_CHAIN) as MutableList<String>
    previousOwners.add(0, community.myPeer.publicKey.toString())
    val transaction = mapOf(
        CONTENT_HASH to torrentHash.toString(),
        OWNERSHIP_CHAIN to previousOwners,
        PRICE to price
        //TransactionRepository.KEY_AMOUNT to BigInteger.valueOf(amount),
        //TransactionRepository.KEY_BALANCE to (BigInteger.valueOf(getMyBalance() - amount).toLong())
    )
    return community.createProposalBlock(
        BLOCK_TYPE_CREATE_CONTENT, transaction,
        EMPTY_PK
    )
}

fun transferNFT(blockHash: String, community: TrustChainCommunity, info : TextView, balance: Float){

    if (blockHash == ""){
        info.setText("No item provided")

    }
    else{
        val Hash = blockHash
        val transferBlock = community.database.getBlockWithHash(Hash.toByteArray())!!
        if (!transferBlock.isProposal || transferBlock.type == BLOCK_TYPE_OPEN_TO_SELL_CONTENT){
            info.setText("Block is not appropriate")
            return
        }
        val price = transferBlock.transaction.get("price") as Float
        if (balance > price){
            info.setText("Not enough money")
        }
        val owner = transferBlock.publicKey
        val transaction = mapOf(
            CONTENT_HASH to transferBlock.transaction.get(CONTENT_HASH),
            OWNERSHIP_CHAIN to transferBlock.transaction.get(OWNERSHIP_CHAIN),
            PRICE to price,
            CONTENT_BLOCK to transferBlock.hashNumber
        )
        community.createProposalBlock(
            BLOCK_TYPE_TRANSFER_NFT,
            transaction,
            owner
        )
    }
}
