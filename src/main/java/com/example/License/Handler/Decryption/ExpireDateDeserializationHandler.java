package com.example.License.Handler.Decryption;

import com.example.License.DTO.LicenseDTO;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.LocalDate;

@RequiredArgsConstructor
public class ExpireDateDeserializationHandler implements LicenseFieldDeserializationHandler {
    private final int BITMASK = 16;
    private final LocalDate EPOCH_DATE = LocalDate.of(2020, 1, 1);
    public int getBitmask(){
        return BITMASK;
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
