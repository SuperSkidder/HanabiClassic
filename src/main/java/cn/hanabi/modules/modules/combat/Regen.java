package cn.hanabi.modules.modules.combat;

import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.math.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C03PacketPlayer;


public class Regen extends Mod {
    private final Value packet = new Value("Regen", "Packets", 10.0D, 1.0D, 1000.0D, 1.0D);
    private final TimeHelper delay = new TimeHelper();
    private final Value<Double> regendelay = new Value("Regen", "Delay", 500.0D, 0.0D, 10000.0D, 100D);

    public Regen() {
        super("Regen", Category.WORLD);
    }

    @EventTarget
    public void onMotion(EventPreMotion event) {
        if (delay.isDelayComplete(regendelay.getValueState().intValue())) {
            if (!ModManager.getModule("Fly").isEnabled()) {
                if (!(mc.thePlayer.fallDistance > 2.0F)) {
                    if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() && mc.thePlayer.getFoodStats().getFoodLevel() >= 19) {
                        if (mc.thePlayer.onGround) {
                            for (int i = 0; (double) i < (Double) this.packet.getValueState(); ++i) {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                                    this.setDisplayName("Health:" + mc.thePlayer.getHealth());
                                    delay.reset();
                                } else {
                                    this.setDisplayName("OtherNoGround");
                                }
                            }
                        } else {
                            this.setDisplayName("NoGround");
                        }
                    } else {
                        this.setDisplayName("MaxHealth");
                    }
                } else {
                    this.setDisplayName("Falling");
                }
            } else {
                this.setDisplayName("Flying");
            }
        }
    }
}
