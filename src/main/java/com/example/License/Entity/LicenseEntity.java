package com.example.License.Entity;

import java.sql.Timestamp;

import com.example.License.DTO.LicenseRequestDTO;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "licenses")
@NoArgsConstructor
@Getter
public class LicenseEntity {


    public LicenseEntity(LicenseRequestDTO dto) {
        this.license_v = dto.license_v();
        this.expiration = dto.expiration();
        this.uuid= dto.uuid();
        this.customerid = dto.customerid();
        this.solution = dto.solution();
        this.publicKey = dto.publicKey();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String license_v;
    private String expiration;
    private String uuid;
    private String customerid;
    private String solution;
    private String publicKey;

    @CreationTimestamp
    private Timestamp createDate;


}

