package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO.Builder;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class CoreCountDeserializationHandler implements LicenseFieldDeserializationHandler {
    private final int BITMASK = 1;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.coreCount(dis.readInt());
            }
        }
    }
}
