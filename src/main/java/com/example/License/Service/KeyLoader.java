package com.example.License.Service;

import lombok.RequiredArgsConstructor;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

// String 으로 저장되어있는 키를 객체로 변환하는 클래스

@Component
@RequiredArgsConstructor
public class KeyLoader {

    @Value("${license.private-key}")
    private String PRIVATE_KEY_STRING;

    @Value("${license.public-key}")
    private String PUBLIC_KEY_STRING="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlZ60iu0ucDxPVnP85UI/8OlUE2IUsZUzagOJsA0Rp7/K5UI/A04oMv3uqVTS4uZcHQGlt3vwbPNWjpKQhLDPY+buISr0950iUdKVKkx3SlknGxFhSfZ5yNBzRwgaSf8emGVzZP3fBtzma4g3HJ7TItK0yZiXKisN6L7f+GvjEy6luIhyUbgy8rzrboZx0lPx1hkhs/PSda/gSjHeEdN7eM1/mSRv4i+iIAYhkHtMTcTA4jx0gM2aWL+5kUoa2dK/Y/FEEJY3xQmxa6xEqZc/C7dFw0RFInBL/BbmivVCFjWsUcoa3hOkPWiOVBFlSItyXtaDkvdLF7ntXej6Hn/v0QIDAQAB";

    public PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPem = PRIVATE_KEY_STRING;
        if (privateKeyPem == null) {
            throw new RuntimeException("입력된 개인키가 없습니다.");
        }
        privateKeyPem = privateKeyPem.replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (PUBLIC_KEY_STRING == null || PUBLIC_KEY_STRING.isBlank()) {
            throw new RuntimeException("application.properties에 'license.public-key'가 설정되지 않았습니다.");
        }

        String key = PUBLIC_KEY_STRING.replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}