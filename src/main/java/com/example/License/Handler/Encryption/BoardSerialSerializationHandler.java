package com.example.License.Handler.Encryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BoardSerialSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 4;
    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getBoardSerial() != null) {
            dos.writeBoolean(true);
            writeString(dos, dto.getBoardSerial());
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
