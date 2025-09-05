package com.example.License.Handler.Encryption;

import java.io.DataOutputStream;
import java.io.IOException;

import com.example.License.DTO.LicenseDTO;


public interface LicenseFieldSerializationHandler {
    // 직렬화: DTO에서 값을 읽어 스트림에 쓴다
    void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException;
    public int getBitmask();

}
