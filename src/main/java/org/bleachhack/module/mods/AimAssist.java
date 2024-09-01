package org.bleachhack.module.mods;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.MathUtil;
import org.bleachhack.util.world.EntityUtils;
import org.bleachhack.util.world.Rot;

public class AimAssist extends Module {

    public AimAssist() {
        super("AimAssist", KEY_UNBOUND, ModuleCategory.COMBAT, "Automatically aims at entities.",
                new SettingSlider("Distance", 3, 10, 10, 2).withDesc("The distance of the aim."),
                new SettingSlider("Smoothness", 0, 10, 6, 2).withDesc("The smoothness of the aim."),
                new SettingToggle("SeeOnly", true).withDesc("Only aims at entities you can see."),
                new SettingToggle("Vertical", true).withDesc("Aim at vertical entities."),
                new SettingToggle("Horizontal", true).withDesc("Aim at horizontal entities."));
    }


    public static boolean isOverEntity() {
        if (mc.crosshairTarget == null) return false;
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            return true;
        } else {
            return false;
        }
    }

    public boolean isHoldingFirework() {
        PlayerInventory inventory = mc.player.getInventory();
        ItemStack heldItem = inventory.getMainHandStack();

        return heldItem.getItem() instanceof FireworkRocketItem;
    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        if (isHoldingFirework()) return;
        if (isOverEntity()) return;
        if (mc.currentScreen != null) return;

        PlayerEntity targetPlayer = EntityUtils.findClosest(PlayerEntity.class, getSetting(0).asSlider().getValueFloat());

        if (targetPlayer == null || (getSetting(2).asToggle().getState() && !mc.player.canSee(targetPlayer))) {
            return;
        }
        Rot targetRot = MathUtil.getDir(mc.player, targetPlayer.getPos());

        float yawDist = MathHelper.subtractAngles((float) targetRot.yaw(), mc.player.getYaw());
        float pitchDist = MathHelper.subtractAngles((float) targetRot.pitch(), mc.player.getPitch());

        float yaw;
        float pitch;

        float stren = getSetting(1).asSlider().getValueFloat() / 10;


        yaw = mc.player.getYaw();
        if (Math.abs(yawDist) > stren) {
            yaw = mc.player.getYaw();
            if (yawDist < 0) {
                yaw += stren;
            } else if (yawDist > 0) {
                yaw -= stren;
            }
        } else {
            // aw = (float) targetRot.yaw();
        }

        pitch = mc.player.getPitch();
        if (Math.abs(pitchDist) > stren) {
            pitch = mc.player.getPitch();
            if (pitchDist < 0) {
                pitch += stren;
            } else if (pitchDist > 0) {
                pitch -= stren;
            }
        } else {
            // pitch = (float) targetRot.pitch();
        }

        float stren2 = getSetting(1).asSlider().getValueFloat() / 50;
        yaw = MathHelper.lerpAngleDegrees(stren2, mc.player.getYaw(), (float) targetRot.yaw());
        pitch = MathHelper.lerpAngleDegrees(stren2, mc.player.getPitch(), (float) targetRot.pitch());
        if (getSetting(3).asToggle().getState()) mc.player.setYaw(yaw);
        if (getSetting(4).asToggle().getState()) mc.player.setPitch(pitch);
    }
}
