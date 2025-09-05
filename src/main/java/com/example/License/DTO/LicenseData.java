package com.example.License.DTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final StringDataReader stringDataReader;

    private List<LicenseFieldSerializationHandler> sHandlers;
    private List<LicenseFieldDeserializationHandler> dHandlers;

    @PostConstruct
    public void initialize() {
        // sHandlers 리스트를 getBitmask()의 오름차순으로 정렬
        sHandlers = Stream.of(
                        // 새로운 필드 핸들러를 여기에 추가!
                        new SocketCountSerializationHandler(),
                        new BoardSerialSerializationHandler(stringDataReader),
                        new MacAddressSerializationHandler(stringDataReader),
                        new ExpireDateSerializationHandler(),
                        new CoreCountSerializationHandler() // 1 번을 가장 뒤로 했을때
                )
                .sorted(Comparator.comparingInt(LicenseFieldSerializationHandler::getBitmask))  //getBitmask() 기준으로 정렬
                .collect(Collectors.toList());
        this.sHandlers = createPaddedList(sHandlers, DummySerializationHandler::new);

        // dHandlers 리스트를 getBitmask()의 오름차순으로 정렬
        dHandlers = Stream.of(
                        // 새로운 필드 핸들러를 여기에 추가!
                        new CoreCountDeserializationHandler(),
                        new SocketCountDeserializationHandler(),
                        new BoardSerialDeserializationHandler(stringDataReader),
                        new MacAddressDeserializationHandler(stringDataReader),
                        new ExpireDateDeserializationHandler()
                )
                .sorted(Comparator.comparingInt(LicenseFieldDeserializationHandler::getBitmask)) // getBitmask() 기준으로 정렬
                .collect(Collectors.toList());
        this.dHandlers = createPaddedList(dHandlers, DummyDeserializationHandler::new);

    }
    public List<LicenseFieldSerializationHandler> SHandlers() {
        return this.sHandlers;
    }

    public List<LicenseFieldDeserializationHandler> DHandlers() {
        return this.dHandlers;
    }
    // LicenseDTO로부터 rawData 생성 (직렬화)
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

    private <T> List<T> createPaddedList(List<? extends T> sortedHandlers, java.util.function.IntFunction<T> dummyFactory) {
        if (sortedHandlers.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> finalList = new ArrayList<>();
        // 기대하는 비트마스크 값 (1, 2, 4, 8, ...)
        long expectedBitmask = 1;
        // 비트 마스크 추출 메소드
        java.util.function.ToIntFunction<T> getBitmaskFunc = (handler) -> {
            if (handler instanceof LicenseFieldSerializationHandler) {
                return ((LicenseFieldSerializationHandler) handler).getBitmask();
            } else if (handler instanceof LicenseFieldDeserializationHandler) {
                return ((LicenseFieldDeserializationHandler) handler).getBitmask();
            }
            return 0; 
        };
        for (T handler : sortedHandlers) {
            int currentBitmask = getBitmaskFunc.applyAsInt(handler);
            // 현재 핸들러의 비트마스크가 기대값보다 클 경우, 그 사이를 더미로 채운다
            while (expectedBitmask < currentBitmask) {
                finalList.add(dummyFactory.apply((int) expectedBitmask));
                expectedBitmask *= 2;
            }
            if (expectedBitmask != currentBitmask) {
                throw new IllegalStateException();
            }
            finalList.add(handler);
            expectedBitmask *= 2;
        }

        return finalList;
    }
}