package com.example.License.notUsed.Handler.Encryption;

import com.example.License.notUsed.LicenseDTO;
import lombok.RequiredArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class SocketCountSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 2;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        // bitmask에 포함될 때만 처리
        if ((dto.getType() & BITMASK) != 0 && dto.getSocketCount() != null) {
            dos.writeBoolean(true);
            dos.writeInt(dto.getSocketCount());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }


}
