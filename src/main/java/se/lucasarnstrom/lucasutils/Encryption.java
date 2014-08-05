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
import org.bukkit.plugin.java.JavaPlugin;

public class Encryption {

    private ConsoleLogger logger;
    private final String ENCRYPTION_KEY;

    public Encryption(JavaPlugin plugin, String encryption_key) {
        logger = new ConsoleLogger(plugin.getName() + "-Encryption");
        ENCRYPTION_KEY = encryption_key;
    }

    /**
     * Encrypts string with AES using the given key.
     *
     * @param input          - String to encrypt.
     * @return - Encrypted string, null if failed to encrypt.
     */
    public String encrypt(String input) {
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }
        catch(Exception e) {
            logger.severe(e.getMessage());
            logger.debug(e.getStackTrace());
        }

        return crypted == null ? null : new String(Base64.encodeBase64(crypted));
    }
}
