package com.example.License.Entity;

import java.sql.Timestamp;

import com.example.License.DTO.LicenseDTORequest;
import com.example.License.Proto.LicenseProtos;
import org.hibernate.annotations.CreationTimestamp;

import com.example.License.DTO.LicenseDTO;

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


    public LicenseEntity(LicenseDTORequest dto) {
        this.coreCount = dto.coreCount();
        this.socketCount = dto.socketCount();
        this.boardSerial = dto.boardSerial();
        this.macAddress = dto.macAddress();
        this.expireDate = dto.expireDate();
        this.type = dto.type();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer coreCount;
    private Integer socketCount;
    private String boardSerial;
    private String macAddress;
    private String expireDate;
    private Integer type;

    @CreationTimestamp
    private Timestamp createDate;


    public LicenseDTO toDTO() {
            return LicenseDTO.builder()
                    .coreCount(this.coreCount)
                    .socketCount(this.socketCount)
                    .boardSerial(this.boardSerial)
                    .macAddress(this.macAddress)
                    .expireDate(this.expireDate)
                    .type(this.type)
                    .build();
    }

    public LicenseProtos.License toProto() {
        return LicenseProtos.License.newBuilder()
                .setCoreCount(this.coreCount != null ? this.coreCount : 0)
                .setSocketCount(this.socketCount != null ? this.socketCount : 0)
                .setBoardSerial(this.boardSerial != null ? this.boardSerial : "")
                .setMacAddress(this.macAddress != null ? this.macAddress : "")
                .setExpireDate(this.expireDate != null ? this.expireDate : "")
                .setType(this.type != null ? this.type : 0)
                .build();
    }
}

