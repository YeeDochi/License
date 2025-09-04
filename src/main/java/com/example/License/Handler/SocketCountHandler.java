package com.example.License.Handler;

import com.example.License.DTO.LicenseDTO;
import com.example.License.DTO.LicenseDTO.Builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketCountHandler implements LicenseFieldHandler {
    private static final int BITMASK = 2;

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        // bitmask에 포함될 때만 처리
        if ((dto.getType() & BITMASK) != 0 && dto.getSocketCount() != null) {
            dos.writeBoolean(true);
            dos.writeInt(dto.getCoreCount());
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }

    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.socketCount(dis.readInt());
            }
        }
    }
}
