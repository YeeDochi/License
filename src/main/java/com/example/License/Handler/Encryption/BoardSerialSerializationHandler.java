package com.example.License.Handler.Encryption;

import com.example.License.DTO.LicenseDTO;
import com.example.License.Handler.StringDataReader;
import lombok.RequiredArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class BoardSerialSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 4;
    private final StringDataReader reader;
    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getBoardSerial() != null) {
            dos.writeBoolean(true);
            reader.writeString(dos, dto.getBoardSerial());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }


}
