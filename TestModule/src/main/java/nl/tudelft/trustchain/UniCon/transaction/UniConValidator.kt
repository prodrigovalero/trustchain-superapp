package nl.tudelft.trustchain.TestModule.transaction

import android.util.Log
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainStore
import nl.tudelft.ipv8.attestation.trustchain.validation.TransactionValidator
import nl.tudelft.ipv8.attestation.trustchain.validation.ValidationResult

open class UniConValidator() : TransactionValidator {
    val BLOCK_TYPE_CREATE_CONTENT = "create_content"
    val BLOCK_TYPE_OPEN_TO_SELL_CONTENT = "sellable_content"

    override fun validate(block: TrustChainBlock, database: TrustChainStore): ValidationResult {
        if (block.type == BLOCK_TYPE_CREATE_CONTENT)  {
            validateUniConCreate(block)
        } else {
            validateUniConSell(block)
        }
        return ValidationResult.Valid
    }

    private fun validateUniConCreate(block: TrustChainBlock) {
        if ((block.transaction.get("price") as Float) < 0){
            throw Exception("Incorrect price")
        }
        if ((block.transaction.get("content_hash")) == null){

            throw Exception("No content hash is given properly")
        }
        if ((block.transaction.get("ownership_chain") as? MutableList<*>) == null ){
            throw Exception("No content hash is given properly")
        }
    }

    private fun validateUniConSell(block: TrustChainBlock) {
        if ((block.transaction.get("price") as Float) < 0){
            throw Exception("Incorrect price")
        }
        if ((block.transaction.get("content_hash")) == null){
            throw Exception("No content hash is given properly")
        }
        if ((block.transaction.get("ownership_chain") as? MutableList<*>) == null ){
            throw Exception("No content hash is given properly")
        }
        Log.i("personal", "MINE: block is: " + block.transaction.get("content_block"))
//        if (database.getBlockWithHash(block.transaction.get("content_block") as ByteArray)!!.type == BLOCK_TYPE_CREATE_CONTENT){
  //          throw Exception("No content hash is given properly")
        //    }

    }
    private fun validateUniConTransfer(block: TrustChainBlock, database: TrustChainStore) {
        if ((block.transaction.get("price") as Float) < 0){
            throw Exception("Incorrect price")
        }
        if ((block.transaction.get("content_hash")) == null){
            throw Exception("No content hash is given properly")
        }
        if ((block.transaction.get("ownership_chain") as? MutableList<*>) == null ){
            throw Exception("No content hash is given properly")
        }
        if (database.getBlockWithHash(block.transaction.get("content_block") as ByteArray)!!.type == BLOCK_TYPE_OPEN_TO_SELL_CONTENT){
            throw Exception("No content hash is given properly")
        }

    }

}
