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

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class Networking {

    private ConsoleLogger logger;

    public Networking(JavaPlugin plugin) {
        logger = new ConsoleLogger(plugin.getName() + "-Networking");
    }

    /**
     * Send data to a webserver using _POST.
     *
     * @param urlString - Url to the server.
     * @param data      - A map with the data to send.
     * @return - Answer from the webserver, null if failed.
     */
    public String sendWebPost(String urlString, Map<String, String> data) {
        String answer = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send data
            DataOutputStream dataOut = new DataOutputStream(con.getOutputStream());

            StringBuilder param = new StringBuilder();

            boolean first = true;
            for(Map.Entry<String, String> e : data.entrySet()) {
                if(first) {
                    param.append(e.getKey());
                    first = false;
                }
                else {
                    param.append('&');
                    param.append(e.getKey());
                }
                param.append('=');
                param.append(URLEncoder.encode(e.getValue(), "UTF-8"));
            }

            dataOut.writeBytes(param.toString());
            dataOut.flush();
            dataOut.close();

            // Receive data
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            answer = in.readLine();

            String debug = answer;
            while(debug != null) {
                logger.debug("POST-Recieve:" + debug);
                debug = in.readLine();
            }

            in.close();
        }
        catch(IOException e) {
            if(logger != null) {
                logger.severe(e.toString());
            }
            e.printStackTrace();
        }

        return answer;
    }
}
