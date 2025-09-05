package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO.Builder;
import java.io.DataInputStream;
import java.io.IOException;

public class SocketCountDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 2;

    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.socketCount(dis.readInt());
            }
        }
    }
}
