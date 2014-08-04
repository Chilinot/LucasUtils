/**
 *  Author:  Lucas Arnström - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
 *
 *
 *  Copyright 2014 Lucas Arnström
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package se.lucasarnstrom.lucasutils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Encryption {

    /**
     * Encrypts string with AES using the given key.
     *
     * @param input          - String to encrypt.
     * @param encryption_key - Encryption key.
     * @return - Encrypted string, null if failed to encrypt.
     */
    public static String encrypt(String input, String encryption_key) {
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(encryption_key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return crypted == null ? null : new String(Base64.encodeBase64(crypted));
    }
}
