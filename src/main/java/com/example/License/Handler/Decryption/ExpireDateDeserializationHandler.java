package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.LocalDate;

public class ExpireDateDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 16;
    private static final LocalDate EPOCH_DATE = LocalDate.of(2020, 1, 1);

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
