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

        sHandlers = Stream.of(
                        // 새로운 필드 핸들러를 여기에 추가!
                        new SocketCountSerializationHandler(), // 2
                        new BoardSerialSerializationHandler(stringDataReader), // 4
                        new MacAddressSerializationHandler(stringDataReader), // 8
                        new ExpireDateSerializationHandler(), // 16
                        new CoreCountSerializationHandler() // 1
                )
                .sorted(Comparator.comparingInt(LicenseFieldSerializationHandler::getBitmask))  //getBitmask() 기준으로 정렬
                .collect(Collectors.toList()); // 정렬!
        // 비트마스크가 불연속할때 빈 곳을 더미로 체워주는 코드
        this.sHandlers = createPaddedList(sHandlers, DummySerializationHandler::new);

        dHandlers = Stream.of(
                        // 새로운 필드 핸들러를 여기에 추가!
                        new CoreCountDeserializationHandler(), // 1
                        new SocketCountDeserializationHandler(), // 2
                        new BoardSerialDeserializationHandler(stringDataReader), // 3
                        new MacAddressDeserializationHandler(stringDataReader), // 4
                        new ExpireDateDeserializationHandler() // 5
                )
                .sorted(Comparator.comparingInt(LicenseFieldDeserializationHandler::getBitmask)) // getBitmask() 기준으로 정렬
                .collect(Collectors.toList()); // 정렬!
        // 비트마스크가 불연속할때 빈 곳을 더미로 체워주는 코드
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

    // 불연속한 비트마스크를 채워주는 메소드 (2가지 인터페이스에서 사용해야하기에 제네릭..)
    // 2개를 따로 만들면 각 메소드는 좀더 간단해진다.
    private <T> List<T> createPaddedList(List<? extends T> sortedHandlers, java.util.function.IntFunction<T> dummyFactory) {
        if (sortedHandlers.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> finalList = new ArrayList<>();
        // 기대하는 비트마스크 값 (1, 2, 4, 8, ...)
        long expectedBitmask = 1;
        // 비트 마스크 추출 메소드 (dummy.getBitmast() -> 제네릭으로)
        java.util.function.ToIntFunction<T> getBitmaskFunc = (handler) -> {
            if (handler instanceof LicenseFieldSerializationHandler) {
                return ((LicenseFieldSerializationHandler) handler).getBitmask();
            } else if (handler instanceof LicenseFieldDeserializationHandler) {
                return ((LicenseFieldDeserializationHandler) handler).getBitmask();
            }
            return 0;
        };
        for (T handler : sortedHandlers) {
            int currentBitmask = getBitmaskFunc.applyAsInt(handler); // 비트마스크 추출
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