package com.example.app_nfc.ui

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_nfc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        
        // Kiểm tra thiết bị có hỗ trợ NFC không
        if (nfcAdapter == null) {
            Toast.makeText(this, "Thiết bị không hỗ trợ NFC", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Tạo PendingIntent cho NFC
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        setupUI()
    }

    private fun setupUI() {
        // TODO: Thêm các thành phần UI và xử lý sự kiện
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Xử lý intent NFC khi phát hiện thẻ
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            // TODO: Xử lý dữ liệu từ thẻ NFC
        }
    }
} 