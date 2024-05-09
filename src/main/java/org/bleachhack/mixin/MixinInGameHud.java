/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventRenderCrosshair;
import org.bleachhack.event.events.EventRenderInGameHud;
import org.bleachhack.event.events.EventRenderOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Unique private boolean bypassRenderOverlay = false;
	@Unique private boolean bypassRenderCrosshair = false;

	@Shadow private void renderOverlay(DrawContext context, Identifier texture, float opacity) {}
	@Shadow private void renderCrosshair(DrawContext context) {}

	@Inject(method = "render", at = @At("RETURN"), cancellable = true)
	private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
		EventRenderInGameHud event = new EventRenderInGameHud(context);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
	private void renderOverlay(DrawContext context, Identifier texture, float opacity, CallbackInfo ci) {
		if (!bypassRenderOverlay) {
			EventRenderOverlay event = new EventRenderOverlay(context, texture, opacity);
			BleachHack.eventBus.post(event);

			if (!event.isCancelled()) {
				bypassRenderOverlay = true;
				renderOverlay(context, event.getTexture(), event.getOpacity());
				bypassRenderOverlay = false;
			}

			ci.cancel();
		}
	}


	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void renderCrosshair(DrawContext context, CallbackInfo ci) {
		if (!bypassRenderCrosshair) {
			EventRenderCrosshair event = new EventRenderCrosshair(context);
			BleachHack.eventBus.post(event);

			if (!event.isCancelled()) {
				bypassRenderCrosshair = true;
				renderCrosshair(context);
				bypassRenderCrosshair = false;
			}

			ci.cancel();
		}
	}
}
