package com.example.License.notUsed.Handler.Decryption;

import com.example.License.notUsed.LicenseDTO.Builder;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class SocketCountDeserializationHandler implements LicenseFieldDeserializationHandler {
    private final int BITMASK = 2;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.socketCount(dis.readInt());
            }
        }
    }
}
