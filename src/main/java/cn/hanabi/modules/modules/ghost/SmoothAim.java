package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.modules.world.AntiBot;
import cn.hanabi.modules.modules.world.Teams;
import cn.hanabi.utils.math.TimeHelper;
import cn.hanabi.utils.rotation.RotationUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public class SmoothAim extends Mod {

    public static Value<Double> range = new Value<>("AimAssist", "Range", 4.5D, 0.0D, 10.0D);
    public static Value<Boolean> attackPlayers = new Value<>("AimAssist", "Players", true);
    public static Value<Boolean> attackAnimals = new Value<>("AimAssist", "Animals", false);
    public static Value<Boolean> attackMobs = new Value<>("AimAssist", "Mobs", false);
    public static Value<Boolean> throughblock = new Value<>("AimAssist", "ThroughBlock", true);
    public static Value<Boolean> invisible = new Value<>("AimAssist", "Invisibles", false);
    public static Value<Boolean> weapon = new Value<>("AimAssist", "OnlyWeapon", false);
    public static Value<Double> rotation2Value = new Value<>("AimAssist", "Yaw Offset", 200d, 0d, 400d, 10d);
    public static Value<Double> rotationValue = new Value<>("AimAssist", "Pitch Offset", 200d, 0d, 400d, 10d);
    public static Value<Boolean> randoms2 = new Value<>("AimAssist", "Pitch Random", true);
    public static ArrayList<EntityLivingBase> targets = new ArrayList<>();
    public static EntityLivingBase target = null;
    public Value<Double> speed = new Value<>("AimAssist", "Speed", 0.1, 0.0, 1.0, 0.01);
    public Value<Double> horizontal = new Value<>("AimAssist", "Horizontal", 4.0, 0.0, 10.0, 0.1);
    public Value<Double> vertical = new Value<>("AimAssist", "Vertical", 2.0, 0.0, 10.0, 0.1);
    public Value<Double> switchsize = new Value<>("AimAssist", "Max Targets", 1.0, 1.0, 5.0, 1.0);
    public Value<Double> switchDelay = new Value<>("AimAssist", "Switch Delay", 50d, 0d, 2000d, 10d);
    public Value<Boolean> clickAim = new Value<>("AimAssist", "Click Aim", false);
    public Value<Boolean> strafe = new Value<>("AimAssist", "Strafe Increase", false);
    public int index;
    float[] lastRotations;
    private final TimeHelper switchTimer = new TimeHelper();


    public SmoothAim() {
        super("AimAssist", Category.GHOST);
    }

    public static float[] getNeededRotations(Vec3 vec) {
        Vec3 playerVector = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        double y = vec.yCoord - playerVector.yCoord;
        double x = vec.xCoord - playerVector.xCoord;
        double z = vec.zCoord - playerVector.zCoord;
        double dff = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dff)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static Vec3 getCenter(AxisAlignedBB bb) {
        double value = Math.random();
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * ((rotation2Value.getValue() / 400)),
                bb.minY + (bb.maxY - bb.minY) * (randoms2.getValue() ? value : (rotationValue.getValue() / 400)), bb.minZ + (bb.maxZ - bb.minZ) * ((rotation2Value.getValue() / 400)));
    }

    private static boolean isValidEntity(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity.isDead || ((EntityLivingBase) entity).getHealth() <= 0f) {
                return false;
            }
            if (mc.thePlayer.getDistanceToEntity(entity) < (range.getValueState())) {
                if (entity != mc.thePlayer && !mc.thePlayer.isDead
                        && !(entity instanceof EntityArmorStand || entity instanceof EntitySnowman)) {

                    if (entity instanceof EntityPlayer && attackPlayers.getValueState()) {

                        if (!mc.thePlayer.canEntityBeSeen(entity) && !throughblock.getValueState())
                            return false;

                        if (entity.isInvisible() && !invisible.getValueState())
                            return false;

                        return !AntiBot.isBot(entity) && !Teams.isOnSameTeam(entity);
                    }

                    if (entity instanceof EntityMob && attackMobs.getValueState()) {
                        return !AntiBot.isBot(entity);
                    }

                    if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                            && attackAnimals.getValueState()) {
                        return !AntiBot.isBot(entity);
                    }
                }
            }
        }
        return false;
    }

    @EventTarget
    public void onPre(EventPreMotion event) {
        if (mc.theWorld == null) {
            return;
        }
        if (mc.thePlayer == null) {
            return;
        }
        if (!mc.thePlayer.isEntityAlive()) {
            return;
        }
        if (clickAim.getValueState() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        if (!holdWeapon()) {
            return;
        }

        // 初始化变量
        if (!targets.isEmpty() && index >= targets.size())
            index = 0; // 超过Switch限制

        for (EntityLivingBase ent : targets) { // 添加实体
            if (isValidEntity(ent))
                continue;
            targets.remove(ent);
        }
        // Switch结束

        getTarget(event); // 拿实体

        if (targets.size() == 0) { // 实体数量为0停止攻击
            target = null;
        } else {
            target = targets.get(index);// 设置攻击的Target
            if (mc.thePlayer.getDistanceToEntity(target) > range.getValueState()) {
                target = targets.get(0);
            }
        }


        // Switch开始
        if (target != null) {
            // Switch开始
            if (target.hurtTime == 10 && switchTimer.isDelayComplete(switchDelay.getValueState() * 1000)
                    && targets.size() > 1) {
                switchTimer.reset();
                ++index;
            }


            lastRotations = getNeededRotations(getCenter(targets.get(index).getEntityBoundingBox()));

            if (lastRotations[1] > 90) {
                lastRotations[1] = 90;
            } else if (lastRotations[1] < -90) {
                lastRotations[1] = -90;
            }

            if (target != null) {

                double horizontalSpeed = horizontal.getValue() * 3.0 + (horizontal.getValue() > 0.0 ? rand.nextDouble() : 0.0);
                double verticalSpeed = vertical.getValue() * 3.0 + (vertical.getValue() > 0.0 ? rand.nextDouble() : 0.0);

                if (strafe.getValue() && mc.thePlayer.moveStrafing != 0.0f) {
                    horizontalSpeed *= 1.25;
                }

                if (target != null) {
                    horizontalSpeed *= speed.getValue();
                    verticalSpeed *= speed.getValue();
                }

                this.faceTarget(target, 0.0f, (float) verticalSpeed);
                this.faceTarget(target, (float) horizontalSpeed, 0.0f);

            }


        } else {
            targets.clear();
            lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }

    }

    private void getTarget(EventPreMotion event) {
        int maxSize = switchsize.getValueState().intValue(); // 最大实体数量

        for (Entity o3 : mc.theWorld.loadedEntityList) { // 遍历实体
            EntityLivingBase curEnt;

            if (o3 instanceof EntityLivingBase && isValidEntity(curEnt = (EntityLivingBase) o3)
                    && !targets.contains(curEnt))
                targets.add(curEnt);
            if (targets.size() >= maxSize)
                break; // 超过了限制跳出
        }


        // 排序目标实体
        targets.sort((o1, o2) -> {
            float[] rot1 = RotationUtil.getRotationToEntity(o1);
            float[] rot2 = RotationUtil.getRotationToEntity(o2);
            return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
        });
    }

    public boolean holdWeapon() {
        if (!weapon.getValue()) {
            return true;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer.getCurrentEquippedItem() == null) {
            return false;
        }
        return mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemTool;
    }

    protected float getRotation(float currentRotation, float targetRotation, float maxIncrement) {
        float deltaAngle = MathHelper.wrapAngleTo180_float(targetRotation - currentRotation);
        if (deltaAngle > maxIncrement) {
            deltaAngle = maxIncrement;
        }
        if (deltaAngle < -maxIncrement) {
            deltaAngle = -maxIncrement;
        }
        return currentRotation + deltaAngle / 2.0f;
    }

    private void faceTarget(Entity target, float yawspeed, float pitchspeed) {
        EntityPlayerSP player = mc.thePlayer;
        float yaw = lastRotations[0];
        float pitch = lastRotations[1];
        player.rotationYaw = this.getRotation(player.rotationYaw, yaw, yawspeed);
        player.rotationPitch = this.getRotation(player.rotationPitch, pitch, pitchspeed);
    }


}
