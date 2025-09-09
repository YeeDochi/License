package com.example.License.DTO;


public record LicenseRequestDTO(
    String license_v,
    String expiration,
    String uuid,
    String customerid,
    String solution,
    String publicKey
) {}
