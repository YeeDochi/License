package com.example.License.Handler.Encryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MacAddressSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 8;

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getMacAddress() != null) {
            dos.writeBoolean(true);
            writeString(dos, dto.getMacAddress());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }



    private void writeString(DataOutputStream dos, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        dos.writeShort(bytes.length);
        dos.write(bytes);
    }


}
