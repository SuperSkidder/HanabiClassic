package cn.hanabi.injection.mixins;

import cn.hanabi.Hanabi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {


    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    public void onInit(CallbackInfo ci) {
        Minecraft.getMinecraft().displayGuiScreen(new me.yarukon.mainmenu.GuiCustomMainMenu());
        ci.cancel();
    }
}
