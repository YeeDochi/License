package com.example.License.notUsed.Handler.Decryption;

import com.example.License.notUsed.LicenseDTO;

import java.io.DataInputStream;
import java.io.IOException;

public interface LicenseFieldDeserializationHandler {

    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException;
    public int getBitmask();
}
