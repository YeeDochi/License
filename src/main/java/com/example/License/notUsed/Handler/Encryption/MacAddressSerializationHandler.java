package com.example.License.notUsed.Handler.Encryption;

import com.example.License.notUsed.LicenseDTO;
import com.example.License.notUsed.Handler.StringDataReader;
import lombok.RequiredArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class MacAddressSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 8;
    private final StringDataReader reader;
    public int getBitmask(){
        return BITMASK;
    }
    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getMacAddress() != null) {
            dos.writeBoolean(true);
            reader.writeString(dos, dto.getMacAddress());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }

}
