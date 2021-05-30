package com.example.nft

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.frostwire.jlibtorrent.*
import com.frostwire.jlibtorrent.alerts.AddTorrentAlert
import com.frostwire.jlibtorrent.alerts.Alert
import com.frostwire.jlibtorrent.alerts.AlertType
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert
import com.frostwire.jlibtorrent.swig.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import nl.tudelft.ipv8.IPv8Configuration
import nl.tudelft.ipv8.OverlayConfiguration
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.android.keyvault.AndroidCryptoProvider
import nl.tudelft.ipv8.attestation.trustchain.ANY_COUNTERPARTY_PK
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.ipv8.attestation.trustchain.TrustChainSettings
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainSQLiteStore
import nl.tudelft.ipv8.keyvault.PrivateKey
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomWalk
import nl.tudelft.ipv8.sqldelight.Database
import java.io.*
import java.lang.Thread.sleep
import java.nio.channels.FileChannel

class Start : Application() {
    init{
        val settings = TrustChainSettings()
        val driver = AndroidSqliteDriver(Database.Schema, this, "trustchain.db")
        val store = TrustChainSQLiteStore(Database(driver))
        val randomWalk = RandomWalk.Factory()
        val trustChainCommunity = OverlayConfiguration(
            TrustChainCommunity.Factory(settings, store),
            listOf(randomWalk)
        )
        val config = IPv8Configuration(overlays = listOf(
            trustChainCommunity
        ))
        Log.i("personal", "MINE: Before calling factory")
        try {

            IPv8Android.Factory(this)
                .setConfiguration(config)
                .setPrivateKey(getPrivateKey())
                .init()
            Log.i("personal", "All went okei")
        } catch (e :Exception) {
            Log.i("personal", "something went wrong")
        }
    }
    private fun getPrivateKey(): PrivateKey {
        return AndroidCryptoProvider.generateKey()
    }
}
class Create_NFT : AppCompatActivity() {
    val BLOCK_TYPE_CREATE_NFT = "create_NFT"
    val NFT_HASH = "torrent_hash"
    val OWNERSHIP_CHAIN = "ownership_chain"
    val PRICE = "price"
    var s : SessionManager
    var sessionActive : Boolean
    val driver = AndroidSqliteDriver(Database.Schema, this, "trustchain.db")
    val store = TrustChainSQLiteStore(Database(driver))

    init {
        this.s = SessionManager()
        this.sessionActive = false
        //IPv8Android.getInstance()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_nft)
        //sleep(50000)
        initializeTorrentSession()
        //changeInfoText("Torrent is not initialized")
        var btn: Button = findViewById(R.id.btnCreate)
        btn.setOnClickListener(){
            createNFT()
        }

    }
    fun changeInfoText(str : String) {
        val text: TextView = findViewById(R.id.torrentView)
        text.setText(str)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun createNFT(){
        changeInfoText("in method createNFT")
        sleep(2000)
        var hash = createTorrent()
        Log.i("personal", "MINE: COMEME LAS PELOTAS" + hash.toString())
        /*var price = findViewById<TextView>(R.id.price) as TextView
        var floatPrice : Float = 0.toFloat()
        val inputTextPrice = price.text.toString()
        if (inputTextPrice != ""){
            try {
                floatPrice = inputTextPrice as Float
            }
            catch (e: Exception){
                floatPrice = 0.toFloat()
            }
        }

        var previousBlockHash = findViewById<TextView>(R.id.previousContent) as TextView
        val inputText = previousBlockHash.text.toString()
        if (inputText == ""){
            newNFT(hash!!, floatPrice)
        }
        else{
            buildUponNFT(hash!!, inputText.toByteArray(), floatPrice)
        }*/


        //var hash = Sha1Hash()
        //storeHash(hash)
    }
    fun newNFT(torrentHash: Sha1Hash, price : Float){
        var trust = IPv8Android.getInstance()
        var community = trust.getOverlay<TrustChainCommunity>()!!
        var previousOwners : MutableList<PublicKey> = ArrayList()
        previousOwners.add(0, community.myPeer.publicKey)
        val transaction = mapOf(
            NFT_HASH to torrentHash,
            OWNERSHIP_CHAIN to previousOwners,
            PRICE to price
            //TransactionRepository.KEY_AMOUNT to BigInteger.valueOf(amount),
            //TransactionRepository.KEY_BALANCE to (BigInteger.valueOf(getMyBalance() - amount).toLong())
        )
        community.createProposalBlock(
            BLOCK_TYPE_CREATE_NFT, transaction,
            ANY_COUNTERPARTY_PK
        )
    }
    fun buildUponNFT( torrentHash : Sha1Hash, previousBlockHash : ByteArray, price : Float){
        var trust = IPv8Android.getInstance()
        var community = trust.getOverlay<TrustChainCommunity>()!!
        var previousBlock = store.getBlockWithHash(previousBlockHash)
        var previousOwners : MutableList<PublicKey> = previousBlock!!.transaction.get(OWNERSHIP_CHAIN) as MutableList<PublicKey>
        previousOwners.add(0, community.myPeer.publicKey)
        val transaction = mapOf(
            NFT_HASH to torrentHash,
            OWNERSHIP_CHAIN to previousOwners,
            PRICE to price
            //TransactionRepository.KEY_AMOUNT to BigInteger.valueOf(amount),
            //TransactionRepository.KEY_BALANCE to (BigInteger.valueOf(getMyBalance() - amount).toLong())
        )
        community.createProposalBlock(
            BLOCK_TYPE_CREATE_NFT, transaction,
            ANY_COUNTERPARTY_PK
        )
    }
    fun initializeTorrentSession() {
        s.addListener(object : AlertListener {
            override fun types(): IntArray? {
                return null
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun alert(alert: Alert<*>) {
                val type = alert.type()

                when (type) {
                    AlertType.ADD_TORRENT -> {
                        Log.i("personal", "Torrent added")
                        (alert as AddTorrentAlert).handle().resume()
                    }
                    AlertType.BLOCK_FINISHED -> {
                        val a = alert as BlockFinishedAlert
                        val p = (a.handle().status().progress() * 100).toInt()
                        var progressBar: ProgressBar? = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
                        if (progressBar != null) {
                            progressBar.setProgress(p, true)
                        }
                        Log.i(
                            "personal",
                            "Progress: " + p + " for torrent name: " + a.torrentName()
                        )
                        Log.i("personal", java.lang.Long.toString(s.stats().totalDownload()))
                    }
                    AlertType.TORRENT_FINISHED -> {
                        var progressBar: ProgressBar? = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
                        if (progressBar != null) {
                            progressBar.setProgress(100, true)
                        }
                        Log.i("personal", "Torrent finished")
                        //printToast("Torrent downloaded!!")
                    }
                    else -> {
                    }
                }
            }
        })
    }


    /*
    Creates a torrent from a file given as input
    The extension of the file must be included (for example, .png)
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @Suppress("deprecation")
    fun createTorrent(): Sha1Hash? {
        val fileName: String?
        var filepath = findViewById<TextView>(R.id.price) as TextView
        val inputText = filepath.text.toString()
        if (inputText == "") {
            Log.i("personal", "MINE: No torrent name given, using default")
            fileName = "Mauri.pdf"
        } else fileName = inputText


        val file =
            File(applicationContext.getExternalFilesDir(null)!!.getAbsolutePath() + "/" + fileName)
        if (!file.exists()) {
            Log.i("personal", "MINE: File doesn't exist!")
            return null
        }

        val fs = file_storage()
        val l1: add_files_listener = object : add_files_listener() {
            override fun pred(p: String): Boolean {
                return true
            }
        }
        libtorrent.add_files_ex(fs, file.absolutePath, l1, create_flags_t())
        val ct = create_torrent(fs)
        val l2: set_piece_hashes_listener = object : set_piece_hashes_listener() {
            override fun progress(i: Int) {}
        }

        val ec = error_code()
        libtorrent.set_piece_hashes_ex(ct, file.parent, l2, ec)
        val torrent = ct.generate()
        val buffer = torrent.bencode()

        var torrentName = fileName.substringBeforeLast('.') + ".torrent"

        var os: OutputStream? = null
        try {
            // uncomment if you want to write to the actual phone storage (needs "write" permission)

            Log.i("personal", "MINE: The file will try to be written in: " + Environment.getExternalStorageDirectory().absolutePath + "/Downloads/" + torrentName )
            Log.i("personal", "MINE: the file :" + File(Environment.getExternalStorageDirectory().absolutePath + "/Downloads/" + torrentName))
                //os = FileOutputStream(File(Environment.getExternalStorageDirectory().absolutePath + "/Downloads/" + torrentName))
            //Log.i("personal", "MINE: " + os)
            Log.i("personal", "MINE: torrentName is: "+ torrentName)

            var path = applicationContext.getExternalFilesDir(null)!!.getAbsolutePath()
            Log.i("personal", "MINE: absolute path" + path)
            var file = File(path + torrentName)
            //os = FileOutputStream(File(applicationContext.getExternalFilesDir(null)!!.getAbsolutePath() + "/Downloads/" + torrentName))
            file.createNewFile()
            file.writeBytes(Vectors.byte_vector2bytes(buffer))
            //os.write(Vectors.byte_vector2bytes(buffer), 0, Vectors.byte_vector2bytes(buffer).size)
            Log.i("personal", "MINE: The file was written theoretically in: " + applicationContext.getExternalFilesDir(null)!!.getAbsolutePath()  + torrentName )
        } catch (e: IOException) {
            e.printStackTrace()
        }


        val ti = TorrentInfo.bdecode(Vectors.byte_vector2bytes(buffer))
        Log.i("personal", "MINE: before calling getTorrent")
        return getTorrent(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Suppress("deprecation")
    fun getTorrent(uploadHappening: Boolean): Sha1Hash? {
        Log.i("personal", "MINE: IN GET TORRENT FILE")
        // Handling of the case where the user is already downloading the
        // same or another torrent
        if (sessionActive) {
            Log.i("personal", "MINE: Session active ")
            s.stop()
            sessionActive = false
            var btn = findViewById(R.id.btnCreate) as Button
            var torrentView = findViewById<TextView>(R.id.torrentView) as TextView
            if (btn.text.equals("STOP")) {
                btn.setText("DOWNLOAD (TORRENT)")
                return null
            } else {
                torrentView.text = "-----"
                var progressBar: ProgressBar? = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
                if (progressBar != null) {
                    progressBar.setProgress(0, true)
                }
            }
        }
        Log.i("personal", "MINE: Arrived at line 146")
        val torrentName: String?
        var filepath = findViewById<TextView>(R.id.price) as TextView
        val inputText = filepath.text.toString()
        if (inputText == "") {
            changeInfoText("No torrent name given, using default")
            Log.i("personal", "No torrent name given, using default")
            //torrentName = "IMG-20210521-WA0000.jpg"
            torrentName = "Mauri.torrent"
            //return null
        } else torrentName = inputText
        Log.i("personal", "MINE: Before getting file")

        // uncomment if you want to read from the actual phone storage (needs "write" permission)

        //var torrent = Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/WhatsApp Images/" + torrentName
        //var torrentPath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + torrentName

        // if (uploadHappening) {
        // val torrent = Environment.getExternalStorageDirectory().absolutePath + "/" + torrentName
        // torrent =
        var torrentPath = applicationContext.getExternalFilesDir(null)!!.getAbsolutePath() + torrentName

        Log.i("personal", "MINE: After getting file from $torrentPath")
        // }
        try {
            if (!readTorrentSuccesfully(torrentPath)) {
                Log.i("personal", "MINE:Something went wrong, check logs")
                return null
            }
        } catch (e: Exception) {
            Log.i("personal", "MINE:Something went wrong, check logs")
            e.printStackTrace()
        }

        val sp = SettingsPack()
        sp.seedingOutgoingConnections(true)
        val params =
            SessionParams(sp)
        s.start(params)

        if (uploadHappening) {
            var torrentView = findViewById<TextView>(R.id.torrentView) as TextView
            torrentView.text = "Starting to upload, please wait..."
        }
        else {
            var torrentView = findViewById<TextView>(R.id.torrentView) as TextView
            torrentView.text = "Starting to download, please wait..."
        }

        val torrentFile = File(torrentPath)
        val ti = TorrentInfo(torrentFile)

        Log.i("personal", "Storage of downloads: " + torrentFile.parentFile!!.toString())

        sessionActive = true
        if (!uploadHappening) {
            var btn = findViewById<Button>(R.id.btnCreate) as Button
            btn.setText("STOP")
        }
        // uncomment if you want to write to the actual phone storage (needs "write" permission)
        s.download(ti, torrentFile.parentFile)
        // val savePath = applicationContext.getExternalFilesDir(null)!!.getAbsolutePath()
        // s.download(ti, File(savePath))
        return ti.infoHash()
    }

    /**
     * Reads a .torrent file and displays information about it on the screen
     * Part of the getTorrent() function
     */
    @Throws(IOException::class)
    fun readTorrentSuccesfully(torrent: String?): Boolean {
        Log.i("personal", "MINE: In readTorrentSuccesfully")
        val torrentFile = File(torrent!!)
        Log.i("personal", "MINE: Torrent file is: $torrentFile, of type " + torrentFile.javaClass.name)

        if (!torrentFile.exists()) {
            Log.i("personal", "MINE: file doesn't exist!")
            return false
        }
        torrentFile.setReadable(true,false)
        //torrentFile.setWritable(true)
        Log.i("personal", "MINE: file exists! and file is readable:" + torrentFile.canRead())
        val ti = TorrentInfo(torrentFile)


        val fc = RandomAccessFile(torrent, "r").channel
        val buffer =
            fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        val ti2 = TorrentInfo(buffer)
        val toPrint = ti.toEntry().toString() + ti2.toEntry().toString()
        Log.i("personal", ti.toEntry().toString())
        Log.i("personal", ti2.toEntry().toString())
        var torrentView = findViewById<TextView>(R.id.torrentView) as TextView
        torrentView.text = toPrint
        return true
    }

}
