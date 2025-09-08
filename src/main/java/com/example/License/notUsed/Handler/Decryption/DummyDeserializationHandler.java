package com.example.License.notUsed.Handler.Decryption;

import com.example.License.notUsed.LicenseDTO;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class DummyDeserializationHandler implements LicenseFieldDeserializationHandler {
    private int BITMASK;

    public DummyDeserializationHandler(int BITMASK){this.BITMASK=BITMASK;}
    public int getBitmask(){
        return BITMASK;
    }

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
           // 아무 행동도 하지 않음
        }
    }

}
