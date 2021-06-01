package utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class EncryptedPassword {
    private final byte[] salt;
    private final byte[] hash;

    public EncryptedPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        salt = new byte[16];
        random.nextBytes(salt);
        hash = generateHash(password, salt);
    }

    public EncryptedPassword(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.salt = Base64.decodeBase64(salt);
        this.hash = generateHash(password, this.salt);
    }

    public String getSalt() {
        return Base64.encodeBase64String(salt);
    }

    public String getHash() {
        return Base64.encodeBase64String(hash);
    }

    private static byte[] generateHash(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }
}
