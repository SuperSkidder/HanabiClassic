package cn.hanabi.api;

import cn.hanabi.Wrapper;
import cn.hanabi.injection.interfaces.IMinecraft;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MicrosoftLogin {

    private static final String CLIENT_ID = "d1ed1b72-9f7c-41bc-9702-365d2cbd2e38";
    private static HttpServer httpServer;

    static {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(17342), 0);
            System.out.println("create login server");
            httpServer.createContext("/", exchange -> {
                System.out.println("new connection");
                Map<String, String> map = new HashMap<>();
                String s1 = exchange.getRequestURI().toString();
                String code = s1.substring(s1.indexOf("=") + 1);
                httpServer.stop(3);
                map.put("client_id", CLIENT_ID);
                map.put("code", code);
                map.put("grant_type", "authorization_code");
                map.put("redirect_uri", "http://127.0.0.1:17342");
                String oauth = HttpUtils.postMAP("https://login.live.com/oauth20_token.srf", map);
                String access_token = HttpUtils.gson().fromJson(oauth, JsonObject.class).get("access_token").getAsString();
                System.out.println(access_token);

                map.clear();
                Map<String, Object> map2 = new HashMap<>();
                map2.put("AuthMethod", "RPS");
                map2.put("SiteName", "user.auth.xboxlive.com");
                map2.put("RpsTicket", "d=" + access_token);
                JsonObject jo = new JsonObject();
                jo.add("Properties", HttpUtils.gson().toJsonTree(map2));
                jo.addProperty("RelyingParty", "http://auth.xboxlive.com");
                jo.addProperty("TokenType", "JWT");
                String s2 = HttpUtils.postJSON("https://user.auth.xboxlive.com/user/authenticate", jo);
                JsonObject jsonObject = HttpUtils.gson().fromJson(s2, JsonObject.class);
                String xbl_token = jsonObject.get("Token").getAsString();
                String xbl_userhash = jsonObject.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();

                System.out.println(xbl_token);

                JsonObject jo2 = new JsonObject();
                JsonObject jop = new JsonObject();
                jop.addProperty("SandboxId", "RETAIL");
                jop.add("UserTokens", HttpUtils.gson().toJsonTree(new String[]{xbl_token}));
                jo2.add("Properties", jop);
                jo2.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                jo2.addProperty("TokenType", "JWT");
                String xsts = HttpUtils.postJSON("https://xsts.auth.xboxlive.com/xsts/authorize", jo2);


                System.out.println(xsts);


                String xsts_token = HttpUtils.gson().fromJson(xsts, JsonObject.class).get("Token").getAsString();
                String xsts_userhash = HttpUtils.gson().fromJson(xsts, JsonObject.class).get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
                JsonObject properties = new JsonObject();
                properties.addProperty("identityToken", "XBL3.0 x=" + xsts_userhash + ";" + xsts_token);
                String minecraftAuth = HttpUtils.postJSON("https://api.minecraftservices.com/authentication/login_with_xbox", properties);

                JsonObject json = HttpUtils.gson().fromJson(minecraftAuth, JsonObject.class);
                String accessToken = json.get("access_token").getAsString();
                String uuid = json.get("username").getAsString();
                System.out.println(uuid);

                System.out.println("Bearer " + accessToken);
                // get profile
                Map<String, String> map3 = new HashMap<>();
                map3.put("Authorization", "Bearer " + accessToken);
                String profile = HttpUtils.get("https://api.minecraftservices.com/minecraft/profile", map3);
                JsonObject profileJson = HttpUtils.gson().fromJson(profile, JsonObject.class);
                String uuid2 = profileJson.get("id").getAsString();
                String name = profileJson.get("name").getAsString();
                System.out.println(uuid2);
                System.out.println(name);
                ((IMinecraft) Minecraft.getMinecraft()).setSession(new Session(name, uuid2, accessToken, "mojang"));
                String result = "Success";

                exchange.sendResponseHeaders(200, result.length());
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(result.getBytes(StandardCharsets.UTF_8));
            });
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean login() {
        AtomicBoolean flag = new AtomicBoolean(false);
        try {
            Map<String, String> map = new HashMap<>();
            map.put("client_id", CLIENT_ID);
            map.put("response_type", "code");
            map.put("redirect_uri", "http://127.0.0.1:17342");
            map.put("scope", "XboxLive.signin%20XboxLive.offline_access");
            String s = HttpUtils.buildUrl("https://login.live.com/oauth20_authorize.srf",
                    map);
            Desktop.getDesktop().browse(URI.create(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return flag.get();

    }
}
