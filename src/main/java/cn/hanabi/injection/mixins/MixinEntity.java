package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IEntity;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.ghost.Hitbox;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;

    public Entity entity;

    @Shadow
    public float height;

    @Shadow
    public boolean onGround;
    @Shadow
    public World worldObj;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Shadow
    private AxisAlignedBB boundingBox;

    @Override
    public int getNextStepDistance() {
        return nextStepDistance;
    }

    @Override
    public void setNextStepDistance(int distance) {
        nextStepDistance = distance;
    }


    @Override
    public int getFire() {
        return fire;
    }

    @Override
    public void setFire(int i) {
        fire = i;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    @Shadow
    protected UUID entityUniqueID;

    @Shadow public abstract UUID getUniqueID();

    /**
     * @author
     */

    @Inject(
            method = "getCollisionBorderSize",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getCollisionBorderSize(CallbackInfoReturnable callbackInfoReturnable) {
        if (ModManager.getModule(Hitbox.class).isEnabled()) {
            double hitBox = Hitbox.getSize();
            callbackInfoReturnable.setReturnValue((float) hitBox);
            callbackInfoReturnable.cancel();
        }
    }


    @Override
    public boolean canEntityBeSeenFixed(Entity entityIn) {
        return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double) height * 0.85, this.posZ),
                new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ)) == null
                || this.worldObj.rayTraceBlocks(
                new Vec3(this.posX, this.posY + (double) height * 0.85, this.posZ),
                new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ)) == null;
    }

}
