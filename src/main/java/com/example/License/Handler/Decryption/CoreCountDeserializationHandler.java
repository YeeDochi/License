package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO.Builder;

import java.io.DataInputStream;
import java.io.IOException;

public class CoreCountDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 1;

    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.coreCount(dis.readInt());
            }
        }
    }
}
