package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventPacket;
import cn.hanabi.injection.interfaces.IKeyBinding;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.math.TimeHelper;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class Blink extends Mod {
    TimeHelper time = new TimeHelper();
    ArrayList<Packet> packets = new ArrayList<>();

    public Blink() {
        super("Blink", Category.PLAYER);
        setState(false);
        // TODO Auto-generated constructor stub
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getEventType() == EventType.SEND) {

                packets.add(event.getPacket());
                event.setCancelled(true);

        }

    }

    private void addPosition() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        Vector3f vec = new Vector3f((float) x, (float) y, (float) z);
        if (mc.thePlayer.movementInput.moveForward != 0.0F || ((IKeyBinding) mc.gameSettings.keyBindJump).getPress()
                || mc.thePlayer.movementInput.moveStrafe != 0.0F) {
        }

    }

    public void onEnable() {
        EventManager.register(this);
        if (mc.thePlayer != null && mc.theWorld != null) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            float yaw = mc.thePlayer.rotationYaw;
            float pitch = mc.thePlayer.rotationPitch;
            EntityOtherPlayerMP ent = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            ent.inventory = mc.thePlayer.inventory;
            ent.inventoryContainer = mc.thePlayer.inventoryContainer;
            ent.setPositionAndRotation(x, y, z, yaw, pitch);
            ent.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1, ent);
        }

        packets.clear();
    }

    public void onDisable() {
        EventManager.unregister(this);
        mc.theWorld.removeEntityFromWorld(-1);

        for (Packet packet : packets) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
            time.reset();
        }

        packets.clear();
    }

}
