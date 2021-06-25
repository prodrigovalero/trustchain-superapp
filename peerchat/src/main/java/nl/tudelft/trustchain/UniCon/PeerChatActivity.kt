package nl.tudelft.trustchain.TestModule

import nl.tudelft.trustchain.common.BaseActivity

class PeerChatActivity : BaseActivity() {
    override val navigationGraph = R.navigation.nav_graph_peerchat
    override val bottomNavigationMenu = R.menu.peerchat_navigation_menu
}
