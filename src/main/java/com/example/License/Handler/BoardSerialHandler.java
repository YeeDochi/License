package com.example.License.Handler;

import com.example.License.DTO.LicenseDTO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BoardSerialHandler implements LicenseFieldHandler {
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

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.boardSerial(readString(dis));
            }
        }
    }
    
    private void writeString(DataOutputStream dos, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        dos.writeShort(bytes.length);
        dos.write(bytes);
    }

    private String readString(DataInputStream dis) throws IOException {
        short length = dis.readShort();
        byte[] bytes = new byte[length];
        dis.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
