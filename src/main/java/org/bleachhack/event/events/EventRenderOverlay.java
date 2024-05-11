/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.event.events;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.bleachhack.event.Event;

public class EventRenderOverlay extends Event {

	private DrawContext context;
	private Identifier texture;
	private float opacity;
	
	public EventRenderOverlay(DrawContext context, Identifier texture, float opacity) {
		this.context = context;
		this.texture = texture;
		this.opacity = opacity;
	}

	public DrawContext getMatrices() {
		return context;
	}

	public Identifier getTexture() {
		return texture;
	}

	public float getOpacity() {
		return opacity;
	}

}
