package com.example.app_nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.app_nfc.databinding.ActivityMainBinding;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            viewModel.setNfcStatus(getString(R.string.nfc_not_supported));
            return;
        }

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                viewModel.setNfcStatus(getString(R.string.nfc_disabled));
            } else {
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
                viewModel.setNfcStatus(getString(R.string.nfc_status));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            
            viewModel.setNfcStatus(getString(R.string.nfc_tap));
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                String tagInfo = readTagInfo(tag);
                viewModel.setNfcResult(tagInfo);
                saveTagInfoToFile(tagInfo);
            }
        }
    }

    private String readTagInfo(Tag tag) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(bytesToHex(tag.getId())).append("\n");
        info.append("Công nghệ: ").append(getTagTechnology(tag)).append("\n");
        info.append("Kích thước: ").append(tag.getTechList().length).append(" công nghệ\n");
        
        for (String tech : tag.getTechList()) {
            info.append("- ").append(tech).append("\n");
        }
        
        return info.toString();
    }

    private String getTagTechnology(Tag tag) {
        if (NfcA.class.getName().equals(tag.getTechList()[0])) return "NFC-A";
        if (NfcB.class.getName().equals(tag.getTechList()[0])) return "NFC-B";
        if (NfcF.class.getName().equals(tag.getTechList()[0])) return "NFC-F";
        if (NfcV.class.getName().equals(tag.getTechList()[0])) return "NFC-V";
        if (MifareClassic.class.getName().equals(tag.getTechList()[0])) return "MIFARE Classic";
        if (MifareUltralight.class.getName().equals(tag.getTechList()[0])) return "MIFARE Ultralight";
        return "Unknown";
    }

    private void saveTagInfoToFile(String tagInfo) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "ThongTinThe_" + timeStamp + ".txt";
            
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "NFC_Info");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append("Thông tin thẻ NFC\n");
            writer.append("Thời gian: ").append(timeStamp).append("\n\n");
            writer.append(tagInfo);
            writer.flush();
            writer.close();

            viewModel.setNfcResult(tagInfo + "\n\nĐã lưu thông tin vào file: " + file.getAbsolutePath());
        } catch (IOException e) {
            viewModel.setNfcResult(tagInfo + "\n\nLỗi khi lưu file: " + e.getMessage());
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }
} 