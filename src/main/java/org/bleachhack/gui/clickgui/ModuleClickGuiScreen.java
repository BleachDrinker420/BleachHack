/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui.clickgui;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.gui.clickgui.window.ClickGuiWindow;
import org.bleachhack.gui.clickgui.window.ModuleWindow;
import org.bleachhack.gui.window.Window;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.module.mods.ClickGui;
import org.bleachhack.util.io.BleachFileHelper;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class ModuleClickGuiScreen extends ClickGuiScreen {
	
	public static ModuleClickGuiScreen INSTANCE = new ModuleClickGuiScreen();

	private TextFieldWidget searchField;

	public ModuleClickGuiScreen() {
		super(Text.literal("ClickGui"));
	}

	public void init() {
		super.init();

		searchField = new TextFieldWidget(textRenderer, 2, 14, 100, 12, Text.empty() /* @LasnikProgram is author lol */);
		searchField.visible = false;
		searchField.setMaxLength(20);
		searchField.setSuggestion("Search here");
		addDrawableChild(searchField);
	}

	public void initWindows() {
		int len = ModuleManager.getModule(ClickGui.class).getSetting(0).asSlider().getValueInt();
		
		int y = 50;
		for (ModuleCategory c: ModuleCategory.values()) {
			addWindow(new ModuleWindow(ModuleManager.getModulesInCat(c), 30, y, len, StringUtils.capitalize(c.name().toLowerCase()), c.getItem()));
			y += 16;
		}

		for (Window w: getWindows()) {
			if (w instanceof ClickGuiWindow) {
				((ClickGuiWindow) w).hiding = true;
			}
		}
	}

	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		BleachFileHelper.SCHEDULE_SAVE_CLICKGUI.set(true);
		ClickGui clickGui = ModuleManager.getModule(ClickGui.class);

		searchField.visible = clickGui.getSetting(1).asToggle().getState();

		if (clickGui.getSetting(1).asToggle().getState()) {
			searchField.setSuggestion(searchField.getText().isEmpty() ? "Search here" : "");

			Set<Module> seachMods = new HashSet<>();
			if (!searchField.getText().isEmpty()) {
				for (Module m : ModuleManager.getModules()) {
					if (m.getName().toLowerCase(Locale.ENGLISH).contains(searchField.getText().toLowerCase(Locale.ENGLISH).replace(" ", ""))) {
						seachMods.add(m);
					}
				}
			}

			for (Window w : getWindows()) {
				if (w instanceof ModuleWindow) {
					((ModuleWindow) w).setSearchedModule(seachMods);
				}
			}
		}

		int len = clickGui.getSetting(0).asSlider().getValueInt();
		for (Window w : getWindows()) {
			if (w instanceof ModuleWindow) {
				((ModuleWindow) w).setLen(len);
			}
		}

		super.render(drawContext, mouseX, mouseY, delta);

		drawContext.drawTextWithShadow(textRenderer, "BleachHack-" + BleachHack.VERSION + "-" + SharedConstants.getGameVersion().getName(), 3, 3, 0x305090);
		drawContext.drawTextWithShadow(textRenderer, "BleachHack-" + BleachHack.VERSION + "-" + SharedConstants.getGameVersion().getName(), 2, 2, 0x6090d0);

		if (clickGui.getSetting(2).asToggle().getState()) {
			drawContext.drawTextWithShadow(textRenderer, "BleachHack-" + BleachHack.VERSION + "Current prefix is: \"" + Command.getPrefix() + "\" (" + Command.getPrefix() + "help)", 2, height - 20, 0x99ff99);
			drawContext.drawTextWithShadow(textRenderer, "BleachHack-" + BleachHack.VERSION + "Use " + Command.getPrefix() + "clickgui to reset the clickgui", 2, height - 10, 0x9999ff);
		}
	}
}
