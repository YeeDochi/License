package com.example.License.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.example.License.DTO.LicenseDTO;
import com.example.License.DTO.LicenseDTO.Builder;

public class CoreCountHandler implements LicenseFieldHandler {
    private static final int BITMASK = 1;

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

    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                builder.coreCount(dis.readInt());
            }
        }
    }
}
