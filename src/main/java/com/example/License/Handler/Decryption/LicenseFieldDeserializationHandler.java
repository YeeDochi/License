package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataInputStream;
import java.io.IOException;

public interface LicenseFieldDeserializationHandler {

    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException;
    public int getBitmask();
}
