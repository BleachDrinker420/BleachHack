/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.event.events;

import org.bleachhack.event.Event;

public class EventKeyPress extends Event {

	private int key;
	private int scanCode;
	private Status status;

	public EventKeyPress(int key, int scanCode, Status status) {
		this.key = key;
		this.scanCode = scanCode;
		this.status = status;
	}

	public int getKey() {
		return key;
	}

	public int getScanCode() {
		return scanCode;
	}

	public static class Global extends EventKeyPress {

		private int action;
		private int modifiers;

		public Global(int key, int scanCode, Status status, int action, int modifiers) {
			super(key, scanCode, status);
			this.action = action;
			this.modifiers = modifiers;
		}

		public int getAction() {
			return action;
		}

		public int getModifiers() {
			return modifiers;
		}

	}

	public static class InWorld extends EventKeyPress {

		public InWorld(int key, int scanCode, Status status) {
			super(key, scanCode, status);
		}

	}

	public static class InChat extends EventKeyPress {

		private int modifiers;

		public InChat(int key, int scanCode, Status status, int modifiers) {
			super(key, scanCode, status);
			this.modifiers = modifiers;
		}

		public int getModifiers() {
			return modifiers;
		}

	}

	public enum Status {
		RELEASED,
		PRESSED
	}
}