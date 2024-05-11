package org.bleachhack.gui.window.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class WindowTextWidget extends WindowWidget {

	private Text text;
	private float scale;
	public boolean shadow;
	public int color;
	public TextAlign align;
	public float rotation;

	public WindowTextWidget(String text, boolean shadow, int x, int y, int color) {
		this(text, shadow, TextAlign.LEFT, x, y, color);
	}

	public WindowTextWidget(Text text, boolean shadow, int x, int y, int color) {
		this(text, shadow, TextAlign.LEFT, x, y, color);
	}

	public WindowTextWidget(String text, boolean shadow, TextAlign align, int x, int y, int color) {
		this(text, shadow, align, 1f, x, y, color);
	}

	public WindowTextWidget(Text text, boolean shadow, TextAlign align, int x, int y, int color) {
		this(text, shadow, align, 1f, x, y, color);
	}

	public WindowTextWidget(String text, boolean shadow, TextAlign align, float scale, int x, int y, int color) {
		this(Text.literal(text), shadow, align, scale, x, y, color);
	}

	public WindowTextWidget(Text text, boolean shadow, TextAlign align, float scale, int x, int y, int color) {
		this(text, shadow, align, scale, 0f, x, y, color);
	}

	public WindowTextWidget(Text text, boolean shadow, TextAlign align, float scale, float rotation, int x, int y, int color) {
		super(x, y, x + mc.textRenderer.getWidth(text), (int) (y + 10 * scale));
		this.text = text;
		this.shadow = shadow;
		this.color = color;
		this.align = align;
		this.scale = scale;
		this.rotation = rotation;
	}

	@Override
	public void render(DrawContext drawContext, int windowX, int windowY, int mouseX, int mouseY) {
		super.render(drawContext, windowX, windowY, mouseX, mouseY);

		float offset = mc.textRenderer.getWidth(text) * align.offset * scale;

		drawContext.getMatrices().push();
		drawContext.getMatrices().scale(scale, scale, 1f);
		drawContext.getMatrices().translate((windowX + x1 - offset) / scale, (windowY + y1) / scale, 0);
		drawContext.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		mc.textRenderer.draw(text, 0, 0, color, shadow, drawContext.getMatrices().peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, 0xf000f0);
		immediate.draw();

		if (text.getStyle() != null && mc.currentScreen != null
				&& mouseX >= windowX + x1 - offset && mouseX <= windowX + x2 - offset && mouseY >= windowY + y1 && mouseY <= windowY + y2) {
			drawContext.getMatrices().push();
			drawContext.getMatrices().translate(0, 0, 250);
			//((AccessorScreen) mc.currentScreen).callRenderTextHoverEffect(drawContext, text.getStyle(), mouseX - (windowX + x1 - (int) offset), mouseY - (windowY + y1));
			drawContext.getMatrices().pop();
		}

		drawContext.getMatrices().pop();
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
		this.x2 = x1 + mc.textRenderer.getWidth(text);
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.x2 = (int) (x1 + mc.textRenderer.getWidth(text) * scale);
		this.y2 = (int) (y1 + 10 * scale);
	}

	public enum TextAlign {
		LEFT(0f),
		MIDDLE(0.5f),
		RIGHT(1f);

		public final float offset;

		TextAlign(float offset) {
			this.offset = offset;
		}
	}

}
