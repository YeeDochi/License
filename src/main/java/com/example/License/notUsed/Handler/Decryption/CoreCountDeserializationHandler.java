package com.example.License.notUsed.Handler.Decryption;

import com.example.License.notUsed.LicenseDTO.Builder;
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
                builder.coreCount(dis.readInt()); // 읽어오는 쪽에서 dto로 만들지 않고 원하는 동작을 삽입
            }
        }
    }
}
