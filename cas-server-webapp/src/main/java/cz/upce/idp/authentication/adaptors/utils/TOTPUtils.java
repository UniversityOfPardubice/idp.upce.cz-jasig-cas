package cz.upce.idp.authentication.adaptors.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.codec.binary.Base32;

/**
 * Copied from @{link https://github.com/parkghost/TOTP-authentication-demo}
 * Stolen from @{link
 * https://raw.github.com/Unicon/cas-addons/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TOTPUtils.java}
 *
 * <strong>NOTE: </strong> in order to use this class in CAS maven war overlays,
 * exclude the following jar in the
 * maven-war-plugin->configuration->overlays->excludes section of local maven
 * overlay pom.xml: WEB-INF/lib/commons-codec-1.4.jar
 *
 * @since 0.5
 */
public class TOTPUtils {

    private static final int DEFAULT_SECRET_SIZE = 10;

    private static final int PASS_CODE_LENGTH = 6;

    private static final String CRYPTO = "HmacSHA1";

    private static final Random rand = new SecureRandom();

    private static final int DEFAULT_INTERVAL = 30;

    private static final int DEFAULT_WINDOW = 4;

    public static String generateSecret() {
        return generateSecret(DEFAULT_SECRET_SIZE);
    }
    
    public static String generateSecret(int secretSize) {

        // Allocating the buffer
        byte[] buffer = new byte[secretSize];

        // Filling the buffer with random numbers.
        rand.nextBytes(buffer);

        // Getting the key and converting it to Base32
        Base32 codec = new Base32();
        byte[] secretKey = Arrays.copyOf(buffer, secretSize);
        byte[] encodedKey = codec.encode(secretKey);
        return new String(encodedKey);
    }

    public static boolean checkCode(String secret, long code) throws NoSuchAlgorithmException, InvalidKeyException {
        return checkCode(secret, code, DEFAULT_INTERVAL, DEFAULT_WINDOW);
    }

    public static boolean checkCode(String secret, long code, int window) throws NoSuchAlgorithmException, InvalidKeyException {
        return checkCode(secret, code, DEFAULT_INTERVAL, window);
    }

    public static boolean checkCode(String secret, long code, int interval, int window) throws NoSuchAlgorithmException, InvalidKeyException {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);

        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        //int window = WINDOW;
        long currentInterval = getCurrentInterval(interval);

        for (int i = -window; i <= window; ++i) {
            long hash = TOTP.generateTOTP(decodedKey, currentInterval + i, PASS_CODE_LENGTH, CRYPTO);

            if (hash == code) {
                return true;
            }
        }

        // The validation code is invalid.
        return false;
    }

    private static long getCurrentInterval(int interval) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return currentTimeSeconds / interval;
    }

}
