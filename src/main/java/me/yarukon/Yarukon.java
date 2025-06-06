package me.yarukon;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.game.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class Yarukon {
    public static Yarukon INSTANCE;

    public final char[] ascii_Chars = new char[256];
    public final char[] all_Chars = new char[65535];

    public File skinPath;
    public final HashMap<String, ResourceLocation> skinCache = new HashMap<>();

    public Yarukon() {
        INSTANCE = this;

        skinPath = new File(Hanabi.INSTANCE.fileManager.fileDir + File.separator + "CustomSkins");

        if(!skinPath.exists()) {
            skinPath.mkdir();
        }

        for(int i = 0; i < ascii_Chars.length; i++) {
            ascii_Chars[i] = (char) i;
        }

        for(int i = 0; i < all_Chars.length; i++) {
            all_Chars[i] = (char) i;
        }
    }

    public ResourceLocation getSkin(String fileName) {
        if(this.skinCache.containsKey(fileName)) {
            return this.skinCache.get(fileName);
        }

        return DefaultPlayerSkin.getDefaultSkin(Minecraft.getMinecraft().getSession().getProfile().getId());
    }

    public void loadSkinFromLocal(String fileName) {
        if (skinCache.containsKey(fileName)) {
            return;
        }

        File path = new File(skinPath.getAbsolutePath() + File.separator + fileName + ".png");

        if (!path.exists()) {
            ChatUtils.info("File " + fileName + ".png not exist!");
            return;
        }

        new Thread(() -> {
            skinCache.put(fileName, null);
            ResourceLocation rl = new ResourceLocation("CustomSkins/" + fileName);
            IImageBuffer iib = new IImageBuffer() {
                ImageBufferDownload ibd = new ImageBufferDownload();

                public BufferedImage parseUserSkin(BufferedImage image) {
                    return image;
                }

                @Override
                public void skinAvailable() {
                    skinCache.put(fileName, rl);
                }
            };

            ThreadDownloadImageData textureArt = new ThreadDownloadImageData(path, null, null, iib);
            Minecraft.getMinecraft().getTextureManager().loadTexture(rl, textureArt);
        }).start();
    }

}
