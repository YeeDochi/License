package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;
import com.example.License.Handler.StringDataReader;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class BoardSerialDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 4;
    private final StringDataReader reader;
    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.boardSerial(reader.readString(dis));
            }
        }
    }

}
