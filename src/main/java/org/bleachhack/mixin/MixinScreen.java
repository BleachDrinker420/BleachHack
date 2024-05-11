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
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventRenderScreenBackground;
import org.bleachhack.event.events.EventRenderTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class MixinScreen {

	@Unique private int lastMX;
	@Unique private int lastMY;

	@Unique private boolean skipTooltip;

	@Shadow private void renderWithTooltip(DrawContext context, int mouseX, int mouseY, float delta) {}

	@Inject(method = "render", at = @At("HEAD"))
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
		lastMX = mouseX;
		lastMY = mouseY;
	}

	@Inject(method = "renderWithTooltip", at = @At("HEAD"), cancellable = true)
	private void renderWithTooltip(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!skipTooltip) {
			EventRenderTooltip event = new EventRenderTooltip((Screen) (Object) this, context, mouseX, mouseY, delta);
			BleachHack.eventBus.post(event);

			if (!event.isCancelled()) {
				skipTooltip = true;
				renderWithTooltip(context, event.getMouseX(), event.getMouseY(), event.getDelta());
				skipTooltip = false;
			}

			ci.cancel();
		} else {
			skipTooltip = false;
		}
	}

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		EventRenderScreenBackground event = new EventRenderScreenBackground(context);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}