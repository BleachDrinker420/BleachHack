/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.setting.module;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.bleachhack.setting.SettingDataHandlers;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class SettingItemList extends SettingList<Item> {

	public SettingItemList(String text, String windowText, Item... defaultItems) {
		this(text, windowText, null, defaultItems);
	}

	public SettingItemList(String text, String windowText, Predicate<Item> filter, Item... defaultItems) {
		super(text, windowText, SettingDataHandlers.ITEM, getAllItems(filter), defaultItems);
	}

	private static Collection<Item> getAllItems(Predicate<Item> filter) {
		return filter == null
				? Registries.ITEM.stream().collect(Collectors.toList())
						: Registries.ITEM.stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void renderItem(MinecraftClient mc, DrawContext drawContext, Item item, int x, int y, int w, int h) {
		if (item == null || item == Items.AIR) {
			super.renderItem(mc, drawContext, item, x, y, w, h);
		} else {
			RenderSystem.getModelViewStack().push();

			float scale = (h - 2) / 16f;
			float offset = 1f / scale;

			RenderSystem.getModelViewStack().scale(scale, scale, 1f);

			drawContext.drawItem(new ItemStack(item), (int) ((x + 1) * offset), (int) ((y + 1) * offset));

			RenderSystem.getModelViewStack().pop();
			RenderSystem.applyModelViewMatrix();
		}
	}

	@Override
	public Text getName(Item item) {
		return item.getName();
	}
}
