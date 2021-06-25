package nl.tudelft.trustchain.TestModule.ui.feed

import com.mattskala.itemadapter.Item
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.trustchain.common.contacts.Contact

data class PostItem(
    val block: TrustChainBlock,
    val contact: Contact?,
    val linkedBlock: TrustChainBlock?,
    val linkedContact: Contact?,
    val replies: List<TrustChainBlock>,
    val likes: List<TrustChainBlock>,
    /**
     * True if I liked this post.
     */
    val liked: Boolean
) : Item()
