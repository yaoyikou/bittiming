package com.bittiming.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

public final class Methods {

    public static String uncompress(byte[] bytes) {
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            GZIPInputStream unzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = unzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            return out.toString();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }
}
