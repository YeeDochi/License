package com.example.License.notUsed.Handler.Encryption;

import java.io.DataOutputStream;
import java.io.IOException;

import com.example.License.notUsed.LicenseDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoreCountSerializationHandler implements LicenseFieldSerializationHandler {
    private final int BITMASK = 1;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        // bitmask에 포함될 때만 처리
        if ((dto.getType() & BITMASK) != 0 && dto.getCoreCount() != null) {
            dos.writeBoolean(true);
            dos.writeInt(dto.getCoreCount());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }

}
