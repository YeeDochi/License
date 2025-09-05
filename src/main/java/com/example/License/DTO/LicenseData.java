package com.example.License.DTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.example.License.Handler.Decryption.*;
import com.example.License.Handler.Encryption.*;
import com.example.License.Handler.StringDataReader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


// DTO의 데이터를 고정 길이 바이트 배열로 변환하는 클래스
@Component
@RequiredArgsConstructor
public class LicenseData {
    
    // 기준 날짜 (Epoch) - 이 날짜로부터 며칠이 지났는지를 저장 (좀더 효율적) 
    private static final LocalDate EPOCH_DATE = LocalDate.of(2020, 1, 1);

    private final StringDataReader stringDataReader;

    // static 키워드 제거
    private List<LicenseFieldSerializationHandler> sHandlers;
    private List<LicenseFieldDeserializationHandler> dHandlers;

    // 의존성 주입이 완료된 후 이 메서드가 호출됨
    @PostConstruct
    public void initialize() {
        sHandlers = List.of(
                // 새로운 필드 핸들러를 여기에 추가!
                new CoreCountSerializationHandler(),
                new SocketCountSerializationHandler(),
                new BoardSerialSerializationHandler(stringDataReader),
                new MacAddressSerializationHandler(stringDataReader),
                new ExpireDateSerializationHandler()
        );

        dHandlers = List.of(
                // 새로운 필드 핸들러를 여기에 추가!
                new CoreCountDeserializationHandler(),
                new SocketCountDeserializationHandler(),
                new BoardSerialDeserializationHandler(stringDataReader),
                new MacAddressDeserializationHandler(stringDataReader),
                new ExpireDateDeserializationHandler()
        );
    }
    public List<LicenseFieldSerializationHandler> SHandlers() {
        return this.sHandlers;
    }

    public List<LicenseFieldDeserializationHandler> DHandlers() {
        return this.dHandlers;
    }

    public byte[] toByteArray(LicenseDTO dto) throws java.io.IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(dto.getType()); // 타입은 먼저 쓴다.
        for (LicenseFieldSerializationHandler handler : SHandlers()) {
            handler.serialize(dos, dto);
        }
        return baos.toByteArray();
    }


    // 바이트 배열로부터 LicenseData 객체 생성 (역직렬화)
    public LicenseDTO fromByteArray(byte[] bytes) throws java.io.IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        int typeInt = dis.readInt();
        LicenseDTO.Builder builder = LicenseDTO.builder().type(typeInt);
        for (LicenseFieldDeserializationHandler handler : DHandlers()) {
            handler.deserialize(dis, builder);
        }

        return builder.build();
    }
}