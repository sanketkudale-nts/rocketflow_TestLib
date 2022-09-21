package com.rocketflyer.rocketflow.utils;

import android.util.Log;

import com.tracki.BuildConfig;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by taktii-swetank on 17-11-2017.
 */

public class EncryptionAndDecryption {
    public String getEncryptData(String str) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String encryptedData = "";
        ApiCrypter apiCrypter = new ApiCrypter();
        if (BuildConfig.DEBUG)
            Log.d("Taktii", "before encryption " + str);

        try {
            encryptedData = ApiCrypter.bytesToHex(apiCrypter.encrypt(str));
            if (BuildConfig.DEBUG)
                Log.d("Taktii", "after encryption " + encryptedData);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedData;
    }


    public String getDecryptData(String encryptedData) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String decryptedResult = "";
        ApiCrypter apiCrypter = new ApiCrypter();
        if (BuildConfig.DEBUG) Log.d("Taktii", "encrypted response " + encryptedData.replace("\uFEFF",""));
        try {
            decryptedResult = new String(apiCrypter.decrypt(encryptedData.replace("\uFEFF","")), StandardCharsets.UTF_8);
            // decryptedResult = new String(apiCrypter.decrypt("7d3fbae11c760dd4c4e283d61c3db9c64386131bc514b0b6b01d711f28fa1d73c9de7a387954a9c1a39b84411d3cdebb58f30c119a984c1db4aed53314e8af44a058266450f057036d6ec351eb1bcd70e903e44e4a4d5951658858925544bf7ce54c209c21f04cfa3d877c044a5047d097b181e05c99ec770c25ce74f1802fe83f9091ec9f769ae1109bb2c4e4d363dcbf78635f2088c1a979849eca1fb149780f96e38a53dff5a6b819e11e1c03b98291e33eba9b14842615a41b40fe580ab0cbce90d9ee02b3e0b30f9f54e8ecc253d345a31c1ed6d21cfb281961a63f55f8b7be818ea5071d4368d78039ff9bef2b2e5b1f9978a7ce0fc231b30e9826929ae481ac061a79f67a6ec92001ad43eb08"), "UTF-8");
            decryptedResult = URLDecoder.decode(decryptedResult, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) Log.d("Taktii", "after decryption " + decryptedResult);


        return decryptedResult;
    }
}
