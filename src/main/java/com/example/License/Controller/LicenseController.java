package com.example.License.Controller;

import com.example.License.DTO.LicenseResponseDTO;
import com.example.License.Proto.LicenseProtos;
import com.example.License.Service.newFormattedLicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.License.DTO.LicenseRequestDTO;
import com.example.License.Entity.LicenseEntity;
import com.example.License.Entity.LicenseRepository;
import com.example.License.notUsed.FormattedLicenseService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final FormattedLicenseService formattedLicenseService;
    private final LicenseRepository licenseRepository;
    private final newFormattedLicenseService newFormattedLicenseService;
    // 라이선스 데이터 저장
    @PostMapping
    public ResponseEntity<Long> createLicense(@RequestBody LicenseRequestDTO dto) {
        LicenseEntity entity = new LicenseEntity(dto);
        LicenseEntity saved = licenseRepository.save(entity);
        return ResponseEntity.ok(saved.getId());
    }

    // 라이선스 키 반환
    @GetMapping("/{id}")
    public ResponseEntity<String> getLicenseKey(@PathVariable Long id) throws Exception {
        LicenseEntity licenseEntity = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid license ID: " + id));
        LicenseProtos.License protoLicense = licenseEntity.toProto();
        String newFormatted = newFormattedLicenseService.createLicenseKey(protoLicense);
        return ResponseEntity.ok(newFormatted);
    }

   
    @GetMapping("/decode")
    public ResponseEntity<LicenseResponseDTO> decodeLicense(@RequestParam String licenseKey) throws Exception {
        LicenseProtos.License decodedProto = newFormattedLicenseService.decodeLicenseKey(licenseKey);
        LicenseResponseDTO responseDto = LicenseResponseDTO.fromProto(decodedProto);

        return ResponseEntity.ok(responseDto);
    }

}
