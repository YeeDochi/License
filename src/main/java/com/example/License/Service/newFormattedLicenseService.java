package com.example.License.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// --- 변경 후 ---
import com.example.License.Proto.LicenseProtos.License; // 생성된 Protobuf 클래스를 import
// --- 변경 전 ---
// import com.example.License.DTO.LicenseDTO;
// import com.example.License.DTO.LicenseData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class newFormattedLicenseService {

    private final int DEFAULT_CHUNK_SIZE = 8;
    private final String SYMMETRIC_ALGORITHM = "AES/GCM/NoPadding";
    private final String ASYMMETRIC_SIGNATURE_ALGORITHM = "SHA256withRSA";

    @Value("${license.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${license.GCM_IV_LENGTH}")
    private int GCM_IV_LENGTH;
    @Value("${license.GCM_TAG_LENGTH}")
    private int GCM_TAG_LENGTH;

    private final KeyLoader keyLoader;
    // private final LicenseData data; // <-- 1. LicenseData 의존성 제거

    // --- 변경 후: 파라미터를 Protobuf 객체로 변경 ---
    public String createLicenseKey(License license) throws Exception {
        System.out.println("CreateKey License val: " + license);
        // --- 변경 후: Protobuf 객체에서 바로 byte 배열로 변환 ---
        byte[] rawData = license.toByteArray();

        // (이하 암호화 로직은 동일)
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        byte[] encryptedData = cipher.doFinal(rawData);

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);

        String base32Encoded = new Base32().encodeToString(byteBuffer.array());
        String replacedPadding = base32Encoded.replace('=', '0');
        return formatBase32ToKey(replacedPadding, DEFAULT_CHUNK_SIZE);
    }

    // --- 변경 후: 파라미터를 Protobuf 객체로 변경 ---
    public String createLicenseKey(License license, int temp) throws Exception {
        System.out.println("CreateKey License val: " + license + ", temp: " + temp);
        // --- 변경 후: Protobuf 객체에서 바로 byte 배열로 변환 ---
        byte[] rawData = license.toByteArray();

        // (이하 서명 로직은 동일)
        int dataLength = rawData.length;
        PrivateKey privateKey = keyLoader.loadPrivateKey();
        Signature ecdsaSign = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(rawData);
        byte[] signature = ecdsaSign.sign();

        ByteBuffer byteBuffer = ByteBuffer.allocate(2 + dataLength + signature.length);
        byteBuffer.putShort((short) dataLength);
        byteBuffer.put(rawData);
        byteBuffer.put(signature);
        byte[] finalBytes = byteBuffer.array();

        String base32Encoded = new Base32().encodeToString(finalBytes);
        String replacedPadding = base32Encoded.replace('=', '0');
        return formatBase32ToKey(replacedPadding, DEFAULT_CHUNK_SIZE);
    }

    // --- 변경 후: 반환 타입을 Protobuf 객체로 변경 ---
    public License decodeLicenseKey(String formattedKey) throws Exception {
        String base32Encoded = formattedKey.replace("-", "").toUpperCase().replaceAll("=", "");
        String restoredBase32 = base32Encoded.replace('0', '=');
        byte[] finalBytes = new Base32().decode(restoredBase32);
        ByteBuffer byteBuffer = ByteBuffer.wrap(finalBytes);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        byte[] encryptedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedData);

        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        // --- 변경 후: byte 배열에서 Protobuf 객체로 파싱 ---
        return License.parseFrom(decryptedData);
    }

    // --- 변경 후: 반환 타입을 Protobuf 객체로 변경 ---
    public License decodeLicenseKey(String formattedKey, int temp) throws Exception {
        String base32Encoded = formattedKey.replace("-", "").toUpperCase().replaceAll("=", "");
        String restoredBase32 = base32Encoded.replace('0', '=');
        byte[] finalBytes = new Base32().decode(restoredBase32);
        ByteBuffer byteBuffer = ByteBuffer.wrap(finalBytes);

        short dataLength = byteBuffer.getShort();
        byte[] rawData = new byte[dataLength];
        byteBuffer.get(rawData);
        byte[] signature = new byte[byteBuffer.remaining()];
        byteBuffer.get(signature);

        PublicKey publicKey = keyLoader.loadPublicKey();
        Signature ecdsaVerify = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(rawData);
        boolean isValid = ecdsaVerify.verify(signature);

        if (isValid) {
            System.out.println("유효한 라이선스입니다.");
            // --- 변경 후: byte 배열에서 Protobuf 객체로 파싱 ---
            return License.parseFrom(rawData);
        } else {
            System.out.println("위조된 라이선스입니다!");
            throw new SecurityException("Invalid license key signature.");
        }
    }

    private String formatBase32ToKey(String str, int chunkSize) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < str.length(); i += chunkSize) {
            if (i > 0) {
                formatted.append("-");
            }
            formatted.append(str.substring(i, Math.min(i + chunkSize, str.length())));
        }
        System.out.println(formatted.toString());
        return formatted.toString();
    }
}