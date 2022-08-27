package net.oneseventhree.learn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Utils
{
    public static String readIS(InputStream inputStream)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                int c = 0;
                while ((c = reader.read()) != -1) sb.append((char) c);
                return sb.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
