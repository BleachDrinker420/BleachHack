/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.event.events.EventKeyPress;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.setting.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(Keyboard.class)
public class MixinKeyboard {

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	private void onKeyEvent(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (action == 2) action = 1;

		switch (action) {
			case 0 -> {
				EventKeyPress.Global event = new EventKeyPress.Global(key, scanCode, EventKeyPress.Status.RELEASED, action, modifiers);
				BleachHack.eventBus.post(event);
				if (event.isCancelled()) callbackInfo.cancel();
			}
			case 1 -> {
				EventKeyPress.Global event = new EventKeyPress.Global(key, scanCode, EventKeyPress.Status.PRESSED, action, modifiers);
				BleachHack.eventBus.post(event);
				if (event.isCancelled()) callbackInfo.cancel();
			}
		}
	}

	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 2), cancellable = true)
	private void onKeyEvent_1(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (action == 2) action = 1;

		if (Option.CHAT_QUICK_PREFIX.getValue() && Command.getPrefix().length() == 1 && key == Command.getPrefix().charAt(0)) {
			MinecraftClient.getInstance().setScreen(new ChatScreen(Command.getPrefix()));
		}

		ModuleManager.handleKey(key);

		switch (action) {
			case 0 -> {
				EventKeyPress.InWorld event = new EventKeyPress.InWorld(key, scanCode, EventKeyPress.Status.RELEASED);
				BleachHack.eventBus.post(event);
				if (event.isCancelled()) callbackInfo.cancel();
			}
			case 1 -> {
				EventKeyPress.InWorld event = new EventKeyPress.InWorld(key, scanCode, EventKeyPress.Status.PRESSED);
				ModuleManager.handleKey(key);
				BleachHack.eventBus.post(event);
				if (event.isCancelled()) callbackInfo.cancel();
			}
		}
	}
}