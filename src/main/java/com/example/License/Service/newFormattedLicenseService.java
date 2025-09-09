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

import com.example.License.Proto.LicenseProtos.License; // 생성된 Protobuf 클래스를 import

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


    public String createLicenseKey(License license) throws Exception {
        byte[] rawData = license.toByteArray();

        // 원본 데이터 서명
        PrivateKey privateKey = keyLoader.loadPrivateKey();
        Signature ecdsaSign = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(rawData);
        byte[] signature = ecdsaSign.sign();

        // [원본 데이터 + 서명]을 하나의 덩어리로 결합
        // 구조: [rawData 길이(short)] + [rawData] + [signature]
        ByteBuffer dataToEncryptBuffer = ByteBuffer.allocate(2 + rawData.length + signature.length);
        dataToEncryptBuffer.putShort((short) rawData.length);
        dataToEncryptBuffer.put(rawData);
        dataToEncryptBuffer.put(signature);
        byte[] dataToEncrypt = dataToEncryptBuffer.array();

        // 결합된 덩어리 전체를 암호화
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        byte[] encryptedData = cipher.doFinal(dataToEncrypt);

        // 최종 결과물- [iv] + [암호화된 데이터]
        ByteBuffer finalBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        finalBuffer.put(iv);
        finalBuffer.put(encryptedData);

        String base32Encoded = new Base32().encodeToString(finalBuffer.array());
        String replacedPadding = base32Encoded.replace('=', '0');
        return formatBase32ToKey(replacedPadding, DEFAULT_CHUNK_SIZE);

    }
    public License decodeLicenseKey(String formattedKey) throws Exception {
        String base32Encoded = formattedKey.replace("-", "").toUpperCase().replaceAll("=", "");
        String restoredBase32 = base32Encoded.replace('0', '=');
        byte[] finalBytes = new Base32().decode(restoredBase32);

        // [iv]와 [암호화된 데이터] 분리
        ByteBuffer byteBuffer = ByteBuffer.wrap(finalBytes);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        byte[] encryptedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedData);

        // 데이터 복호화 (AES/GCM)
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        // 복호화 결과물: [rawData 길이] + [rawData] + [signature]
        byte[] decryptedBytes = cipher.doFinal(encryptedData);

        // [rawData]와 [signature] 분리
        ByteBuffer decryptedBuffer = ByteBuffer.wrap(decryptedBytes);
        short rawDataLength = decryptedBuffer.getShort();
        byte[] rawData = new byte[rawDataLength];
        decryptedBuffer.get(rawData);
        byte[] signature = new byte[decryptedBuffer.remaining()];
        decryptedBuffer.get(signature);

        // 서명 검증 (RSA)
        KeyLoader keyLoader = new KeyLoader();
        PublicKey publicKey = keyLoader.loadPublicKey();
        Signature ecdsaVerify = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaVerify.initVerify(publicKey);
        // 원본 데이터(rawData)로 서명을 검증
        ecdsaVerify.update(rawData);
        boolean isValid = ecdsaVerify.verify(signature);

        if (isValid) {
            // 검증 성공 시 원본 데이터를 Protobuf 객체로 변환하여 반환
            return License.parseFrom(rawData);
        } else {
            // 서명 검증 실패 시 null 반환
            return null;
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