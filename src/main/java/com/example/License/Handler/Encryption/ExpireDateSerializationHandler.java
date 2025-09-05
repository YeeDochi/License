package com.example.License.Handler.Encryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ExpireDateSerializationHandler implements LicenseFieldSerializationHandler {
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

}
