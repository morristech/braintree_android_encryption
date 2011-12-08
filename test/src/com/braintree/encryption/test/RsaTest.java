package com.braintree.encryption.test;

import com.braintree.encryption.Aes;
import com.braintree.encryption.Rsa;

import android.test.AndroidTestCase;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import static javax.crypto.Cipher.getInstance;

public class RsaTest extends AndroidTestCase {
    private String publicKey =
        "MIIBCgKCAQEA8wQ3PXFYuBn9RBtOK3lW4V+7HNjik7FFd0qpPsCVd4KeiIfhuzupSevHUOLjbRSqwvAaZK3/icbBaM7CMAR5y0OjAR5lmmEEkc" +
        "w+A7pmKQK6XQ8j3fveJCzC3MPiNiFfr+vER7O4diTxGhoXjFFJQpzKkCwFgwhKrW8uJLmWqVhQRVNphii1GpxI4fjFNc4h1w2W2CJ9kkv+9e3B" +
        "nCpdVe1w7gBQZMkgjCzxbuAg8XaKlKD48M9kr8iE8kNt1eXV0jbmhCY3vZrckCUv26r2X4cD5lDvUtC1Gj6jBFobm/MelAfoFqNeq+/9VyMdYf" +
        "hIecQimiBYr7Vm5VH9m69TXwIDAQAB";
    private String privateKey =
        "MIIEowIBAAKCAQEA8wQ3PXFYuBn9RBtOK3lW4V+7HNjik7FFd0qpPsCVd4KeiIfhuzupSevHUOLjbRSqwvAaZK3/icbBaM7CMAR5y0OjAR5lmm" +
        "EEkcw+A7pmKQK6XQ8j3fveJCzC3MPiNiFfr+vER7O4diTxGhoXjFFJQpzKkCwFgwhKrW8uJLmWqVhQRVNphii1GpxI4fjFNc4h1w2W2CJ9kkv+" +
        "9e3BnCpdVe1w7gBQZMkgjCzxbuAg8XaKlKD48M9kr8iE8kNt1eXV0jbmhCY3vZrckCUv26r2X4cD5lDvUtC1Gj6jBFobm/MelAfoFqNeq+/9Vy" +
        "MdYfhIecQimiBYr7Vm5VH9m69TXwIDAQABAoIBAEvL/9LJPKvHZ2hLv/jtUrze2ASqXRlFzG3lup4ZAUWSVxIsl6qHdEjbIoLHEbpfHNfKfeDz" +
        "KGX3uTGQc574dmiAwyHBMl2RbxRuiNUu2VhnQmtuInjFa0cLMwgajL7nb+n19nWKx7kJ0q2af8fDPr9pGgEXyexRtMEdkV3hCO3uQxA0MlX/61" +
        "LK4Gssk7hlXcNw6k4fIRt9xANnN3KUrGIYtmaCk9kKsX8HhW9yrVm0WWXHnzm6o5O+3BeP+3cWe+NHeRJEVEXwIPqWtdQa6e0hDtLpCQPOSlpr" +
        "4yJHssT2BHpkPaHi6OnGIHa0HD7ibyfwc1KQjcwA8jg4OabmT7ECgYEA/2Y1m1zG5B0nB9Mixu/3K7d+FYpBrlTKElJ0rFNcNBgvt32dPBD6E6" +
        "ZrL8MokXSy8HrhhR4epQYolyfHHtpzva1gZ8XRO1wZO++TfJwfUd1epDGcdMOfw++dZFW1EaWrnC3YPxrvfd/DuilwXg1QUb9aIiXCMpmQw/sm" +
        "0VNk2ycCgYEA85aMzCSR2pMNip9WDQnsrP+6nYhST3BrJlJAwLNrWS9zQFfXLvufIJ0OQkdP2mAM9yN9vV8FmAt7CSAPY2UsMvKpriyv5vlqZZ" +
        "F7VwMr1bOaIOllBA+IIY/x3c7iF5Ezt1hJyNegjmts+Fz39G6PN1WDrCGcmcZbXOEYhs2eyQkCgYEAgANqITpqkpIuKxTgHJjQ6j+p2gAXldr4" +
        "AiEETA/oalApMq6qrh3QSyMiHKmUXvwAaNseyMtlDtA8bi9I9iUG2G7boIgdrMQn/cvCwDW82Rq9Qk1/n2MiZGJpII55GKRSlRDBkDffDNeo0l" +
        "nM8cd4l9Dyy6TjZttkHWd4eHl1VwcCgYAt9VC5T4kJUUdzyR5GNYInHdTK1iaZgF9nCovXD8MIP7CiCjC6V5UtZRSEosnJLOglVNfre9slVb0v" +
        "+pGMslEFh81F5H6HuLU/VpSL1ThXCJzi6sY5XujTVEJRFDCKO8YjKJA7SZusY05bCcdqodV5njPKrUjLpqYkPwAOpwr3aQKBgGie+R5Xk1t0IE" +
        "dTnnY/aZHNHR6fn5elFArgRN6fixx82kQDfgMaeQbtOW4Z8RxDDUeGhc11S1filfVZT2DHayoQLr6ORU/nODhHe6KedsUNFy1IRgoR1Si+2Y1g" +
        "3IjrxqAFFdmgBNsxc1JMoFUDMJe2KlaF3nEk3OWuPc/A5G12";

    private byte[] decrypt(String encrypted) {
        byte[] keyBytes = Base64.decode(privateKey, Base64.NO_WRAP);
        KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
      PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);
      Cipher rsa = getInstance("RSA/ECB/PKCS1Padding");
      rsa.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decodedEncrypted = Base64.decode(encrypted, Base64.NO_WRAP);
      return rsa.doFinal(decodedEncrypted);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
    }

	public void testRSAEncryption() {
		Rsa rsa = new Rsa(publicKey);
		byte[] dataToEncrypt = new Aes().generateKey();
		String encryptedData = rsa.encrypt(dataToEncrypt);
		byte[] decryptedData = decrypt(encryptedData);
		assertTrue(Arrays.equals(dataToEncrypt, Base64.decode(decryptedData, Base64.NO_WRAP)));
	}
}
