package com.example.License.DTO;


public record LicenseRequestDTO(
    Integer coreCount,
    Integer socketCount,
    String boardSerial,
    String macAddress,
    String expireDate,
    Integer type
) {}
