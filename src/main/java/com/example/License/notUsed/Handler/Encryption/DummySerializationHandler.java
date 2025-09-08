package com.example.License.notUsed.Handler.Encryption;

import com.example.License.notUsed.LicenseDTO;
import lombok.RequiredArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class DummySerializationHandler implements LicenseFieldSerializationHandler {
    private int BITMASK =0;

   public DummySerializationHandler(int BITMASK){this.BITMASK=BITMASK;}
    public int getBitmask(){
        return BITMASK;
    }

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
       
        // 항상 null만 삽입
        dos.writeBoolean(false);
        
    }
}
