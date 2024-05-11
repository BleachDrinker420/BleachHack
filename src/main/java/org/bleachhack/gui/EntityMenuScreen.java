/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.module.mods.EntityMenu;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.Boxes;
import org.bleachhack.util.collections.MutablePairList;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/lasnikprogram">Lasnik</a>
 */
public class EntityMenuScreen extends Screen {

	private LivingEntity entity;
	private String focusedString;
	private int crosshairX, crosshairY, focusedDot = -1;
	private float yaw, pitch;

	public EntityMenuScreen(LivingEntity entity) {
		super(Text.literal("Interaction Screen"));
		this.entity = entity;
	}

	public void init() {
		super.init();
		this.cursorMode(GLFW.GLFW_CURSOR_HIDDEN);
		yaw = client.player.getYaw();
		pitch = client.player.getPitch();
	}

	private void cursorMode(int mode) {
		double x = this.client.getWindow().getWidth() / 2d;
		double y = this.client.getWindow().getHeight() / 2d;

		KeyBinding.unpressAll();
		InputUtil.setCursorParameters(this.client.getWindow().getHandle(), GLFW.GLFW_CURSOR_HIDDEN, x, y);
	}

	public void tick() {
		if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(),
				GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_RELEASE) {
			close();
		}
	}

	public void close() {
		cursorMode(GLFW.GLFW_CURSOR_NORMAL);

		// This makes the magic
		if (focusedString != null) {
			DecimalFormat coordFormat = new DecimalFormat("#.##");

			String message = ModuleManager.getModule(EntityMenu.class)
					.interactions.getValue(focusedString)
					.replaceAll("%name%", entity.getDisplayName().getString())
					.replaceAll("%uuid%", entity.getUuidAsString())
					.replaceAll("%health%", String.valueOf((int) entity.getHealth()))
					.replaceAll("%x%", coordFormat.format(entity.getX()))
					.replaceAll("%y%", coordFormat.format(entity.getY()))
					.replaceAll("%z%", coordFormat.format(entity.getZ()));

			if (message.startsWith(">suggest ")) {
				client.setScreen(new ChatScreen(message.substring(9)));
			} else if (message.startsWith(">url ")) {
				try {
					Util.getOperatingSystem().open(new URI(message.substring(5)));
				} catch (Exception e) {
					BleachLogger.error("Invalid url \"" + message.substring(5) + "\"");
				}

				client.setScreen(null);
			} else {
				client.player.networkHandler.sendChatMessage(message);
				client.setScreen(null);
			}
		} else {
			client.setScreen(null);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		// Draw entity
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, InventoryScreen.BACKGROUND_TEXTURE);

		int entitySize = (int) (120 / Boxes.getCornerLength(entity.getBoundingBox()));
		int entityHeight = entitySize / 2 - (int) (10 / Boxes.getAxisLength(entity.getBoundingBox(), Axis.Y));
		InventoryScreen.drawEntity(drawContext,
				(width - entitySize) / 2, (height - entitySize) / 2,
				(width + entitySize) / 2 + entitySize, (height + entitySize) / 2 + entitySize,
				entitySize, 0,
				(float) (width / 2) - mouseX,
				(float) (height / 2 + entityHeight - 45) - mouseY,
				entity
		);

		// Fake crosshair
		RenderSystem.setShaderTexture(0, 0);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
				GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		drawContext.drawTexture(OPTIONS_BACKGROUND_TEXTURE, crosshairX - 8, crosshairY - 8, 0, 0, 15, 15);

		drawDots(drawContext, (int) (Math.min(height, width) / 2 * 0.75), mouseX, mouseY);

		drawContext.getMatrices().push();
		drawContext.getMatrices().scale(2.5f, 2.5f, 1f);
		drawContext.drawCenteredTextWithShadow(textRenderer, entity.getDisplayName().getString(), width / 5, 5, 0xFFFFFFFF);
		drawContext.getMatrices().pop();

		Vector2 center = new Vector2(width / 2, height / 2);
		Vector2 mouse = new Vector2(mouseX, mouseY).subtract(center).normalize();

		int scale = Math.max(1, client.options.getGuiScale().getValue());

		// Move crossHair based on distance between mouse and center. But with limit
		float hypot = (float) Math.hypot(width / 2 - mouseX, height / 2 - mouseY);
		mouse.multiply(Math.min(hypot, 1f) / scale * 200f);

		this.crosshairX = (int) mouse.x + width / 2;
		this.crosshairY = (int) mouse.y + height / 2;

		client.player.setYaw(yaw + mouse.x / 3);
		client.player.setPitch(MathHelper.clamp(pitch + mouse.y / 3, -90f, 90f));
		super.render(drawContext, mouseX, mouseY, delta);
	}

	private void drawDots(DrawContext drawContext, int radius, int mouseX, int mouseY) {
		MutablePairList<String, String> map = ModuleManager.getModule(EntityMenu.class).interactions;
		List<Vector2> pointList = new ArrayList<>();
		String[] cache = new String[map.size()];

		int i = 0;
		double lowestDistance = Double.MAX_VALUE;
		for (String string: map.getEntries()) {
			// Just some fancy calculations to get the positions of the dots
			double s = (double) i / map.size() * 2 * Math.PI;
			int x = (int) Math.round(radius * Math.cos(s) + width / 2);
			int y = (int) Math.round(radius * Math.sin(s) + height / 2);
			drawTextField(drawContext, x, y, string);

			// Calculate lowest distance between mouse and dot
			if (Math.hypot(x - mouseX, y - mouseY) < lowestDistance) {
				lowestDistance = Math.hypot(x - mouseX, y - mouseY);
				focusedDot = i;
			}

			cache[i] = string;
			pointList.add(new Vector2(x, y));
			i++;
		}

		// Go through all point and if it is focused -> drawing different color, changing closest string value
		for (Vector2 point: pointList) {
			if (pointList.get(focusedDot).equals(point)) {
				drawDot(drawContext, (int) point.x, (int) point.y, 0xFF4CFF00);
				this.focusedString = cache[focusedDot];
			} else {
				drawDot(drawContext, (int) point.x, (int) point.y, 0xFF0094FF);
			}
		}
	}

	private void drawRect(DrawContext drawContext, int startX, int startY, int width, int height, int colorInner,int colorOuter) {
		drawContext.drawHorizontalLine(startX, startX + width, startY, colorOuter);
		drawContext.drawHorizontalLine(startX, startX + width, startY + height, colorOuter);
		drawContext.drawVerticalLine(startX, startY, startY + height, colorOuter);
		drawContext.drawVerticalLine(startX + width, startY, startY + height, colorOuter);
		drawContext.fill(startX + 1, startY + 1, startX + width, startY + height, colorInner);
	}

	private void drawTextField(DrawContext drawContext, int x, int y, String text) {
		if (x >= width / 2) {
			drawRect(drawContext, x + 10, y - 8, textRenderer.getWidth(text) + 3, 15, 0x80808080, 0xFF000000);
			drawContext.drawTextWithShadow(textRenderer, text, x + 12, y - 4, 0xFFFFFFFF);
		} else {
			drawRect(drawContext, x - 14 - textRenderer.getWidth(text), y - 8, textRenderer.getWidth(text) + 3, 15, 0x80808080, 0xFF000000);
			drawContext.drawTextWithShadow(textRenderer, text, x - 12 - textRenderer.getWidth(text), y - 4, 0xFFFFFFFF);
		}
	}

	// Literally drawing it in code
	private void drawDot(DrawContext drawContext, int centerX, int centerY, int colorInner) {
		// Black background
		drawContext.fill(centerX - 1, centerY - 5, centerX + 2, centerY + 6, 0xff000000);
		drawContext.fill(centerX - 3, centerY - 4, centerX + 4, centerY + 5, 0xff000000);
		drawContext.fill(centerX - 4, centerY - 3, centerX + 5, centerY + 4, 0xff000000);
		drawContext.fill(centerX - 5, centerY - 1, centerX + 6, centerY + 2, 0xff000000);

		// Fill
		drawContext.fill(centerX - 1, centerY - 4, centerX + 2, centerY + 5, colorInner);
		drawContext.fill(centerX - 3, centerY - 3, centerX + 4, centerY + 4, colorInner);
		drawContext.fill(centerX - 4, centerY - 1, centerX + 5, centerY + 2, colorInner);

		// Light overlay
		drawContext.fill(centerX - 1, centerY - 3, centerX + 1, centerY - 2, 0x80ffffff);
		drawContext.fill(centerX - 2, centerY - 2, centerX - 1, centerY - 1, 0x80ffffff);
		//fill(matrix, centerX - 3, centerY - 1, centerX - 2, centerY, 0x80ffffff);
	}
}


// Creating my own Vector class beacause I couldn't find a good one in minecrafts code
class Vector2 {

	public final float x, y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 normalize() {
		float mag = getMag();

		if (mag == 0 || mag == 1)
			return this;

		return divide(mag);
	}

	public Vector2 subtract(Vector2 vec) {
		return new Vector2(this.x - vec.x, this.y - vec.y);
	}

	public Vector2 divide(float n) {
		return new Vector2(this.x / n, this.y / n);
	}

	public Vector2 multiply(float n) {
		return new Vector2(this.x * n, this.y * n);
	}

	private float getMag() {
		return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(x);
		result = 31 * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		Vector2 other = (Vector2) obj;
		return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
	}
}
