/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bleach.hack.module.mods;

import bleach.hack.event.events.EventReadPacket;
import bleach.hack.module.Category;
import bleach.hack.module.Module;
import bleach.hack.utils.FabricReflect;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

/**
 * @author sl
 * First Module utilizing EventBus!
 */

public class NoVelocity extends Module {
    public NoVelocity() {
        super("NoVelocity", KEY_UNBOUND, Category.PLAYER, "If you take some damage, you don't move. Maybe.");
    }

    //The name of the method doesn't matter nor does it need to be consistent between modules, what matters is the argument.
    @Subscribe
    public void readPacket(EventReadPacket event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
            if (packet.getId() == mc.player.getEntityId()) {
                FabricReflect.writeField(packet, 0, "field_12563", "velocityX");
                FabricReflect.writeField(packet, 0, "field_12562", "velocityY");
                FabricReflect.writeField(packet, 0, "field_12561", "velocityZ");
            }
        } else if (event.getPacket() instanceof ExplosionS2CPacket) {
            FabricReflect.writeField(event.getPacket(), 0, "field_12176", "playerVelocityX");
            FabricReflect.writeField(event.getPacket(), 0, "field_12182", "playerVelocityY");
            FabricReflect.writeField(event.getPacket(), 0, "field_12183", "playerVelocityZ");
        }
    }
    //TODO implement this anti push from Kami??
    //@EventHandler
    //private Listener<EntityEvent.EntityCollision> entityCollisionListener = new Listener<>(event -> {
    //    if (event.getEntity() == mc.player) {
    //        if (horizontal == 0 && vertical == 0) {
    //            event.cancel();
    //            return;
    //        }
    //        event.setX(-event.getX() * horizontal);
    //        event.setY(0);
    //        event.setZ(-event.getZ() * horizontal);
    //    }
    //});
    //
    //@EventHandler
    //private Listener<MoveEntityFluidEvent> moveEntityFluidEventListener = new Listener<>(event -> {
    //    if (event.getEntity() == mc.player) {
    //        event.setMovement(event.getMovement().multiply(horizontal, vertical, horizontal));
    //    }
    //});
}