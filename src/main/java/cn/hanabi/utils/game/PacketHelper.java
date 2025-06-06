package cn.hanabi.utils.game;

import cn.hanabi.utils.math.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public enum PacketHelper {

    instance("instance", 0);

    private static int packetsPerSecondTemp = 0;
    private static int packetsPerSecond;
    private static long lastMS;
    private static boolean doneOneTime;
    private static long startTime;
    private static long lastReceiveTime;
    public static double tps;
    public static double lastTps;
    private static final List<Float> tpsList = new ArrayList();
    private static final float listTime = 300.0f;
    private static int tempTicks = 0;
    public static float fiveMinuteTPS;
    private static final TimeHelper th = new TimeHelper();
    private static final DecimalFormat df = new DecimalFormat();

    static {
        fiveMinuteTPS = 0.0f;
    }

    PacketHelper(String s, int n2) {
    }

    public static void onPacketReceive(Packet event) {
        lastTps = tps;
        if (event instanceof S01PacketJoinGame) {
            tps = 20.0;
            fiveMinuteTPS = 20.0f;
        }
        if (event instanceof S03PacketTimeUpdate) {
            long currentReceiveTime = System.currentTimeMillis();
            if (lastReceiveTime != -1L) {
                long timeBetween = currentReceiveTime - lastReceiveTime;
                double neededTps = (double)timeBetween / 50.0;
                double niceTps = 20.0;
                double multi = neededTps / 20.0;
                tps = 20.0 / multi;
                if (tps < 0.0) {
                    tps = 0.0;
                }
                if (tps > 20.0) {
                    tps = 20.0;
                }
            }
            lastReceiveTime = currentReceiveTime;
        }
        if (event instanceof S03PacketTimeUpdate || event instanceof S00PacketKeepAlive) {
            ++packetsPerSecondTemp;
        }
    }

    public static void onUpdate() {
        if (th.isDelayComplete(2000L) && getServerLagTime() > 5000L) {
            th.reset();
            tps /= 2.0;
        }
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
            tpsList.clear();
        }
        float tteemmpp = 0.0f;
        if (tempTicks >= 20) {
            tpsList.add((float) tps);
            tempTicks = 0;
        }
        if ((float)tpsList.size() >= listTime) {
            tpsList.clear();
            tpsList.add((float) tps);
        }
        for (Float aFloat : tpsList) {
            tteemmpp += aFloat;
        }
        fiveMinuteTPS = tteemmpp / (float)tpsList.size();
        ++tempTicks;
        if (System.currentTimeMillis() - lastMS >= 1000L) {
            lastMS = System.currentTimeMillis();
            packetsPerSecond = packetsPerSecondTemp;
            packetsPerSecondTemp = 0;
        }
        if (packetsPerSecond < 1) {
            if (!doneOneTime) {
                startTime = System.currentTimeMillis();
                doneOneTime = true;
            }
        } else {
            if (doneOneTime) {
                doneOneTime = false;
            }
            startTime = 0L;
        }
    }

    public static long getServerLagTime() {
        if (startTime <= 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - startTime;
    }

    public static char getTPSColorCode(double tps2) {
        if (tps2 >= 17.0) {
            return 'a';
        }
        if (tps2 >= 13.0) {
            return 'e';
        }
        if (tps2 > 9.0) {
            return 'c';
        }
        return '4';
    }
}
