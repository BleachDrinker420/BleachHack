/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.bleachhack.module.ModuleManager;
import org.bleachhack.module.mods.NoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.chunk.light.ChunkSkyLightProvider;

@Mixin(ChunkSkyLightProvider.class)
public class MixinChunkSkylightProvider {

	@Inject(at = @At("HEAD"), method = "method_51531", cancellable = true)
	private void recalculateLevel(long blockPos, long l, int lightLevel, CallbackInfo ci) {
		if (ModuleManager.getModule(NoRender.class).isWorldToggled(4)) {
			ci.cancel();
		}
	}
}
