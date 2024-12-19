package TGBUG.tgbug_edgs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.Bukkit.getLogger;

public class getAPI {
    static void GetAPI(String URL, Callback cb) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                result.append(response);
            }
            else {
                getLogger().warning("获取失败: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cb.onResult(result.toString());
    }

    interface Callback {
        void onResult(String result);
    }
}
