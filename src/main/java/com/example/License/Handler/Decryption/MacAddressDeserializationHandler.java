package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MacAddressDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 8;

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.macAddress(readString(dis));
            }
        }
    }

    private String readString(DataInputStream dis) throws IOException {
        short length = dis.readShort();
        byte[] bytes = new byte[length];
        dis.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
