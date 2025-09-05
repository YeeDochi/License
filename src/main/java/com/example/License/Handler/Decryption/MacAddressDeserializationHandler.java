package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;
import com.example.License.Handler.StringDataReader;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class MacAddressDeserializationHandler implements LicenseFieldDeserializationHandler {
    private final int BITMASK = 8;
    private final StringDataReader reader;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.macAddress(reader.readString(dis));
            }
        }
    }

}
