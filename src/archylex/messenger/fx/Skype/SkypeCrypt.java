package archylex.messenger.fx.Skype;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.IntStream;

public class SkypeCrypt {

    public static String getMac256Hash(String timepost) {
        String appId = "msmsgs@msnmsgr.com";
        String key = "Q1P7W2E4J9R8U3S5";

        String result = "";

        String clearText = timepost + appId;
        clearText += "0".repeat(8 - clearText.length() % 8);

        int cchClearText = clearText.length() / 4;
        List<Long> pClearText = new ArrayList<Long>();

        for (int i : IntStream.range(0, cchClearText).toArray()) {
            pClearText.add(i, 0L);

            for (int pos : IntStream.range(0, 4).toArray()) {
                pClearText.set(i, pClearText.get(i) + (long) (clearText.charAt(4 * i + pos) * Math.pow(256, pos)));
            }
        }

        List<Long> sha256hash = new ArrayList<Long>(Collections.nCopies(4, 0L));

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String str = timepost + key;
            byte[] encodedhash = md.digest(str.getBytes(StandardCharsets.UTF_8));
            String hash = bytesToHex(encodedhash).toUpperCase();

            for (int i : IntStream.range(0, sha256hash.size()).toArray()) {
                for (int pos : IntStream.range(0, 4).toArray()) {
                    int dpos = 8 * i + pos * 2;
                    sha256hash.set(i, sha256hash.get(i) + (long) (Integer.parseInt(hash.substring(dpos, dpos + 2), 16) * Math.pow(256, pos)));
                }
            }

            List<Long> machash = cs64(pClearText, sha256hash);

            List<Long> longxor = new LinkedList<Long>();

            for (int i = 0; i < 4; i++) {
                longxor.add(i, sha256hash.get(i) ^ machash.get(i%2));
            }

            for (int i = 0; i < 4; i++) {
                Integer _int = longxor.get(i) != null ? longxor.get(i).intValue() : null;
                result += int32ToHexString(_int);
            }

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String int32ToHexString(int n) {
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        String hexString = "";

        for (int i : IntStream.range(0, 4).toArray()) {
            hexString += hexChars[(n >> (i * 8 + 4)) & 15];
            hexString += hexChars[(n >> (i * 8)) & 15];
        }

        return hexString;
    }

    private static List<Long> cs64(List<Long> pdwData, List<Long> pInHash) {
        List<Long> result = new LinkedList<>();
        List<Long> cs64 = new LinkedList<>();
        long modulus = 2147483647;
        long qwMAC = 0;
        long qwSum = 0;
        int pos = 0;

        for (int i = 0; i < 4; i++) {
            cs64.add(i, pInHash.get(i) & modulus);
        }

        cs64.add(4, Long.valueOf("242854337"));

        while (pos / 2 < pdwData.size() / 2) {
            long qwDatum = (pdwData.get(pos++) * cs64.get(4)) % modulus;

            for (int i = 0; i < 2; i++) {
                long tmpDatum = i == 0 ? qwDatum : pdwData.get(pos++);
                qwMAC = ((qwMAC + tmpDatum) * cs64.get(0 + i * 2) + cs64.get(1 + i * 2)) % modulus;
                qwSum += qwMAC;
            }
        }

        qwMAC = (qwMAC + cs64.get(1)) % modulus;
        qwSum = (qwSum + cs64.get(3)) % modulus;

        result.add(0, qwMAC);
        result.add(1, qwSum);

        return result;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);

            if(hex.length() == 1) hexString.append('0');

            hexString.append(hex);
        }

        return hexString.toString();
    }
}
