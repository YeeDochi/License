package com.example.License.Controller;

import com.example.License.DTO.LicenseBody;
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


    private final LicenseRepository licenseRepository;
    private final newFormattedLicenseService newFormattedLicenseService;
    // 라이선스 키 저장 및 반환
    @PostMapping("/issue")
    public ResponseEntity<String> getLicenseKey(@RequestBody LicenseRequestDTO dto) throws Exception {
        LicenseEntity entity = new LicenseEntity(dto);
        LicenseEntity saved = licenseRepository.save(entity);
        String[] newFormatted = newFormattedLicenseService.createLicenseKey(dto);
        return ResponseEntity.ok(newFormatted[0]);
    }

    @GetMapping("/decode")
    public ResponseEntity<LicenseBody> decodeLicense(@RequestParam String licenseKey) throws Exception {
        LicenseBody responseDto = newFormattedLicenseService.decodeLicenseKey(licenseKey);
        return ResponseEntity.ok(responseDto);
    }

}
