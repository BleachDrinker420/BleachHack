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
import org.bleachhack.event.Event;

public class EventRenderCrosshair extends Event {

	private DrawContext context;

	public EventRenderCrosshair(DrawContext context) {
		this.setMatrices(context);
	}

	public DrawContext getMatrices() {
		return context;
	}

	public void setMatrices(DrawContext context) {
		this.context = context;
	}
}
