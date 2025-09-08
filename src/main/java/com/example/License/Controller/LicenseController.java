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

import com.example.License.DTO.LicenseDTO;
import com.example.License.DTO.LicenseDTORequest;
import com.example.License.Entity.LicenseEntity;
import com.example.License.Entity.LicenseRepository;
import com.example.License.Service.FormattedLicenseService;

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
    public ResponseEntity<Long> createLicense(@RequestBody LicenseDTORequest dto) {
//       LicenseDTO domainDto = LicenseDTO.builder()
//                .coreCount(dto.coreCount())
//                .socketCount(dto.socketCount())
//                .boardSerial(dto.boardSerial())
//                .macAddress(dto.macAddress())
//                .expireDate(dto.expireDate())
//                .type(dto.type())
//                .build();

        // Entity 생성 및 저장 로직은 domainDto를 사용
        //LicenseEntity entity = new LicenseEntity(domainDto);
        LicenseEntity entity = new LicenseEntity(dto);
        LicenseEntity saved = licenseRepository.save(entity);
        return ResponseEntity.ok(saved.getId());
    }

    // 라이선스 키 반환
    @GetMapping("/{id}")
    public ResponseEntity<String> getLicenseKey(@PathVariable Long id) throws Exception {
       LicenseEntity licenseEntity = licenseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid license ID: " + id));

    // 2. Entity를 DTO로 변환
    //LicenseDTO dto = licenseEntity.toDTO();
    LicenseProtos.License protoLicense = licenseEntity.toProto();
    // 3. 변환된 DTO를 서비스에 전달
    //String formatted = formattedLicenseService.createLicenseKey(dto,1);
    String newFormatted = newFormattedLicenseService.createLicenseKey(protoLicense,2);
    return ResponseEntity.ok(newFormatted);
    }

   
    @GetMapping("/decode")
    public ResponseEntity<LicenseResponseDTO> decodeLicense(@RequestParam String licenseKey) throws Exception {
        //LicenseDTO decoded = formattedLicenseService.decodeLicenseKey(licenseKey,1);

        LicenseProtos.License decodedProto = newFormattedLicenseService.decodeLicenseKey(licenseKey, 1);
        // 2. Protobuf 객체를 클라이언트에게 보낼 ResponseDTO로 변환
        LicenseResponseDTO responseDto = LicenseResponseDTO.fromProto(decodedProto);

        return ResponseEntity.ok(responseDto);
    }

}
