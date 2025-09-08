package com.example.License.DTO;

import com.example.License.Proto.LicenseProtos.License;

// Protobuf 객체로부터 생성될 수 있는, API 응답 전용 DTO
public record LicenseResponseDTO(
        int coreCount,
        int socketCount,
        String boardSerial,
        String macAddress,
        String expireDate
) {
    // Protobuf 객체를 이 DTO로 변환
    public static LicenseResponseDTO fromProto(License proto) {
        return new LicenseResponseDTO(
                proto.getCoreCount(),
                proto.getSocketCount(),
                proto.getBoardSerial(),
                proto.getMacAddress(),
                proto.getExpireDate()
        );
    }
}