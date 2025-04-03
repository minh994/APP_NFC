package com.example.app_nfc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> nfcStatus = new MutableLiveData<>();
    private final MutableLiveData<String> nfcResult = new MutableLiveData<>();

    public MainViewModel() {
        nfcStatus.setValue("Đang chờ thẻ NFC...");
    }

    public LiveData<String> getNfcStatus() {
        return nfcStatus;
    }

    public LiveData<String> getNfcResult() {
        return nfcResult;
    }

    public void setNfcStatus(String status) {
        nfcStatus.setValue(status);
    }

    public void setNfcResult(String result) {
        nfcResult.setValue(result);
    }
} 