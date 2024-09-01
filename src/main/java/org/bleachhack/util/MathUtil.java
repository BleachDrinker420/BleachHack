package org.bleachhack.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.bleachhack.util.world.Rot;

public class MathUtil {

    public static Rot getDir(Entity entity, Vec3d vec) {
        double dx = vec.x - entity.getX(),
                dy = vec.y - entity.getY(),
                dz = vec.z - entity.getZ(),
                dist = MathHelper.sqrt((float) (dx * dx + dz * dz));

        return new Rot(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0), -MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dy, dist))));
    }
}
