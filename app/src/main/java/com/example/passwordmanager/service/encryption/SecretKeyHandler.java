package com.example.passwordmanager.service.encryption;

import android.content.Context;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecretKeyHandler {
    private static final String keyFileName="secret.key";

    public static SecretKey getKey(Context activityContext) throws IOException, NoSuchAlgorithmException {
        SecretKey secretKey=null;
        File file=activityContext.getFileStreamPath(keyFileName);
        String key;
        if(file.exists())
        {
            BufferedReader bufferedReader=new BufferedReader(
                    new InputStreamReader(
                            activityContext
                                    .openFileInput(keyFileName)
                    )
            );
            key=bufferedReader.readLine();
            byte [] keyBytes= Base64.decode(key,0);
            secretKey=new SecretKeySpec(keyBytes,0, keyBytes.length, KeyGeneratorModule.AES);
        }
        else
        {
             secretKey = KeyGeneratorModule.createAESKey();
             saveKey(activityContext,secretKey);
        }

        return secretKey;
    }

    private static void saveKey(Context activityContext,SecretKey secretKey)
            throws IOException
    {
        String key= Base64.encodeToString(secretKey.getEncoded(),0);
        FileOutputStream fileOutputStream=activityContext
                    .openFileOutput(
                            keyFileName,
                            activityContext.MODE_PRIVATE
                    );
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(key);
            outputStreamWriter.flush();
            outputStreamWriter.close();
    }
}
