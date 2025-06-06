package cn.hanabi.command.commands;

import cn.hanabi.Hanabi;
import cn.hanabi.command.Command;
import cn.hanabi.gui.classic.notifications.Notification;
import cn.hanabi.utils.client.ClientUtil;
import cn.hanabi.utils.color.Colors;
import cn.hanabi.utils.waypoints.Waypoint;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class WaypointCommands extends Command {

    public WaypointCommands() {
        super("waypoint", "wp");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        try {
            if (args == null) {
                ClientUtil.sendClientMessage(".waypoint add/del <name> or add <name> <x> <y> <z>", Notification.Type.INFO);
            } else {
                if (args.length > 1) {
                    if (!args[0].contains("d") && !args[0].contains("del")) {
                        if (args[0].contains("a") || args[0].contains("add")) {
                            int color;
                            if (args.length == 2) {
                                if (Hanabi.INSTANCE.waypointManager.containsName(args[1])) {
                                    color = Colors.getColor((int) (255.0D * Math.random()), (int) (255.0D * Math.random()), (int) (255.0D * Math.random()));
                                    Hanabi.INSTANCE.waypointManager.createWaypoint(args[1], new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ), color, mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
                                    ClientUtil.sendClientMessage("Waypoint " + args[1] + " has been successfully created.", Notification.Type.SUCCESS);
                                } else {
                                    ClientUtil.sendClientMessage("Waypoint " + args[1] + " already exists.", Notification.Type.ERROR);
                                }
                            } else
                                if (args.length == 5) {
                                    if (Hanabi.INSTANCE.waypointManager.containsName(args[1])) {
                                        color = Colors.getColor((int) (255.0D * Math.random()), (int) (255.0D * Math.random()), (int) (255.0D * Math.random()));
                                        Hanabi.INSTANCE.waypointManager.createWaypoint(args[1], new Vec3(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), Colors.GREY.c , mc.getCurrentServerData().serverIP);
                                        ClientUtil.sendClientMessage("Waypoint " + args[1] + " has been successfully created.", Notification.Type.SUCCESS);
                                    } else {
                                        ClientUtil.sendClientMessage("Waypoint " + args[1] + " already exists.", Notification.Type.ERROR);
                                        ClientUtil.sendClientMessage(".waypoint add/del <name> or add <name> <x> <y> <z>", Notification.Type.INFO);
                                    }
                                } else {
                                    ClientUtil.sendClientMessage(".waypoint add/del <name> or add <name> <x> <y> <z>", Notification.Type.INFO);
                                }
                        }
                    } else
                        if (args.length == 2) {
                            Iterator<Waypoint> var2 = Hanabi.INSTANCE.waypointManager.getWaypoints().iterator();

                            Waypoint waypoint;
                            do {
                                if (!var2.hasNext()) {
                                    ClientUtil.sendClientMessage("No Waypoint under the name " + args[1] + " was found.", Notification.Type.ERROR);
                                    return;
                                }

                                waypoint = var2.next();
                            } while (!waypoint.getName().contains(args[1]));

                            Hanabi.INSTANCE.waypointManager.deleteWaypoint(waypoint);
                            ClientUtil.sendClientMessage("Waypoint " + args[1] + " has been removed.", Notification.Type.SUCCESS);
                        } else {
                            ClientUtil.sendClientMessage(".waypoint add/del <name> or add <name> <x> <y> <z>", Notification.Type.INFO);
                        }
                } else {
                    if (args[0].contains("c") || args[0].contains("clear")) {
                        ClientUtil.sendClientMessage("Waypoint removed.", Notification.Type.ERROR);
                        Hanabi.INSTANCE.waypointManager.clearWaypoint();
                    } else {
                        ClientUtil.sendClientMessage(".waypoint add/del <name> or add <name> <x> <y> <z>", Notification.Type.INFO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
