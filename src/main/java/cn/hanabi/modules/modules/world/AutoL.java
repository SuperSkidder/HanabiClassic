package cn.hanabi.modules.modules.world;

import cn.hanabi.Hanabi;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.game.AbuseUtil;
import cn.hanabi.utils.math.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;


public class AutoL extends Mod {

    public static final Value<Boolean> ad = new Value("AutoL", "AD", true);
    public static final Value<Boolean> wdr = new Value("AutoL", "AutoReport", true);
    public static final Value<Boolean> clientname = new Value("AutoL", "ClientName", true);
    public static final Value<Boolean> abuse = new Value("AutoL", "Abuse", false);
    public static TimeHelper LTimer = new TimeHelper();
    public static List<String> wdred = new ArrayList();

    public static List<EntityPlayer> power = new ArrayList<>();

    public AutoL() {
        super("AutoL", Category.WORLD);

    }

    public static String getAutoLMessage(String PlayerName) {
        String abuse = AbuseUtil.getAbuseGlobal();

        return (AutoL.clientname.getValueState() ? "[" + Hanabi.CLIENT_NAME + " " + Hanabi.CLIENT_VERSION + "] " : " ") + PlayerName + " L"
                + (AutoL.abuse.getValueState() ? " " + abuse : "");
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName("English");
    }

}





