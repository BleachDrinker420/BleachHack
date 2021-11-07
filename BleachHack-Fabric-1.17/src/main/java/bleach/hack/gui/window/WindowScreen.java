/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package bleach.hack.gui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.ints.Int2IntMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class WindowScreen extends Screen {

	private List<Window> windows = new ArrayList<>();

	// <Layer, Window Index>
	private Int2IntSortedMap windowOrder = new Int2IntRBTreeMap(); 

	public WindowScreen(Text title) {
		super(title);
	}

	public Window addWindow(Window window) {
		windows.add(window);
		windowOrder.put(windows.size() - 1, windows.size() - 1);
		return window;
	}

	public void removeWindow(int index) {
		if (index >= 0 && index < windows.size()) {
			int layer = getWindowLayer(index);

			windows.remove(index);
			windowOrder.remove(layer);
			for (Entry e: new Int2IntRBTreeMap(windowOrder).int2IntEntrySet()) {
				if (e.getIntKey() > layer) {
					windowOrder.remove(e.getIntKey());
					windowOrder.put(e.getIntKey() - 1, e.getIntValue());
				}
			}
		}
	}

	public Window getWindow(int i) {
		return windows.get(i);
	}

	public void clearWindows() {
		windows.clear();
		windowOrder.clear();
	}

	public boolean hasAnyWindows() {
		return windows.size() != 0;
	}

	public List<Window> getWindows() {
		return windows;
	}

	protected List<Integer> getWindowsBackToFront() {
		return new ArrayList<>(windowOrder.values());
	}

	protected List<Integer> getWindowsFrontToBack() {
		List<Integer> w = getWindowsBackToFront();
		Collections.reverse(w);
		return w;
	}
	
	protected int getWindowLayer(int index) {
		return windowOrder.int2IntEntrySet().stream().filter(i -> i.getIntValue() == index).findFirst().get().getIntKey();
	}

	protected int getSelectedWindow() {
		for (int i = 0; i < windows.size(); i++) {
			if (!getWindow(i).closed && getWindow(i).selected) {
				return i;
			}
		}

		return -1;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int sel = getSelectedWindow();

		if (sel == -1) {
			for (int i: getWindowsFrontToBack()) {
				if (!getWindow(i).closed) {
					selectWindow(i);
					break;
				}
			}
		}

		boolean close = true;

		for (int w: getWindowsBackToFront()) {
			if (!getWindow(w).closed) {
				close = false;
				onRenderWindow(matrices, w, mouseX, mouseY);
			}
		}

		if (close) this.onClose();

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void onRenderWindow(MatrixStack matrices, int window, int mouseX, int mouseY) {
		if (!windows.get(window).closed) {
			windows.get(window).render(matrices, mouseX, mouseY);
		}
	}

	public void selectWindow(int window) {
		for (int i = 0; i < windows.size(); i++) {
			Window w = windows.get(i);

			if (i == window) {
				w.closed = false;
				w.selected = true;
				int layer = getWindowLayer(window);

				windowOrder.remove(layer);
				for (Entry e: new Int2IntRBTreeMap(windowOrder).int2IntEntrySet()) {
					if (e.getIntKey() > layer) {
						windowOrder.remove(e.getIntKey());
						windowOrder.put(e.getIntKey() - 1, e.getIntValue());
					}
				}

				windowOrder.put(windowOrder.size(), window);
			} else {
				w.selected = false;
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		/* Handle what window will be selected when clicking */
		for (int wi: getWindowsFrontToBack()) {
			Window w = getWindow(wi);

			if (mouseX >= w.x1 && mouseX <= w.x2 && mouseY >= w.y1 && mouseY <= w.y2 && !w.closed) {
				if (w.shouldClose((int) mouseX, (int) mouseY)) {
					w.closed = true;
					break;
				}
				
				if (!w.selected)
					selectWindow(wi);

				w.mouseClicked(mouseX, mouseY, button);
				break;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (Window w : windows) {
			w.mouseReleased(mouseX, mouseY, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	public void tick() {
		for (Window w : windows) {
			w.tick();
		}

		super.tick();
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (Window w : windows) {
			w.keyPressed(keyCode, scanCode, modifiers);
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public boolean charTyped(char chr, int modifiers) {
		for (Window w : windows) {
			w.charTyped(chr, modifiers);
		}

		return super.charTyped(chr, modifiers);
	}

	public void renderBackgroundTexture(int vOffset) {
		int colorOffset = (int) ((System.currentTimeMillis() / 75) % 100);
		if (colorOffset > 50)
			colorOffset = 50 - (colorOffset - 50);

		// smooth
		colorOffset = (int) (-(Math.cos(Math.PI * (colorOffset / 50d)) - 1) / 2 * 50);

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(width, 0, 0).color(30, 20, 80, 255).next();
		bufferBuilder.vertex(0, 0, 0).color(30 + colorOffset / 3, 20, 80, 255).next();
		bufferBuilder.vertex(0, height + 14, 0).color(90, 54, 159, 255).next();
		bufferBuilder.vertex(width, height + 14, 0).color(105 + colorOffset, 54, 189, 255).next();
		tessellator.draw();

		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}
}
