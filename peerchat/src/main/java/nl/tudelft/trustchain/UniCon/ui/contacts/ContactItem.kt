package nl.tudelft.trustchain.TestModule.ui.contacts

import com.mattskala.itemadapter.Item
import nl.tudelft.trustchain.common.contacts.Contact
import nl.tudelft.trustchain.TestModule.entity.ChatMessage

data class ContactItem(
    val contact: Contact,
    val lastMessage: ChatMessage?,
    val isOnline: Boolean,
    val isBluetooth: Boolean
) : Item() {
    override fun areItemsTheSame(other: Item): Boolean {
        return other is ContactItem && contact.mid == other.contact.mid
    }
}
