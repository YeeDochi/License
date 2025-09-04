package com.example.License.Handler;

import com.example.License.DTO.LicenseDTO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ExpireDateHandler implements LicenseFieldHandler {
    private static final int BITMASK = 16;
    private static final LocalDate EPOCH_DATE = LocalDate.of(2020, 1, 1);

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getExpireDate() != null) {
            dos.writeBoolean(true);
            LocalDate expireDate = LocalDate.parse(dto.getExpireDate());
            long days = ChronoUnit.DAYS.between(EPOCH_DATE, expireDate);
            dos.writeInt((int) days);
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false);
        }
    }

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                int days = dis.readInt();
                builder.expireDate(EPOCH_DATE.plusDays(days).toString());
            }
        }
    }
}
