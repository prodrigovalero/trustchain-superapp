package nl.tudelft.trustchain.TestModule.ui.conversation

import com.mattskala.itemadapter.Item
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.trustchain.TestModule.entity.ChatMessage

data class ChatMessageItem(
    val chatMessage: ChatMessage,
    val transaction: TrustChainBlock?,
    val shouldShowAvatar: Boolean,
    val shouldShowDate: Boolean,
    val participantName: String
) : Item() {
    override fun areItemsTheSame(other: Item): Boolean {
        return other is ChatMessageItem && other.chatMessage.id == chatMessage.id
    }
}
