package dev.urmomia.systems.modules.render.hud;

import dev.urmomia.rendering.text.TextRenderer;
import dev.urmomia.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    public double delta;
    private final List<Runnable> postTasks = new ArrayList<>();

    public void begin(double scale, double tickDelta, boolean scaleOnly) {
        TextRenderer.get().begin(scale, scaleOnly, false);

        this.delta = tickDelta;
    }

    public void end() {
        TextRenderer.get().end();

        for (Runnable runnable : postTasks) {
            runnable.run();
        }

        postTasks.clear();
    }

    public void text(String text, double x, double y, Color color) {
        TextRenderer.get().render(text, x, y, color, true);
    }

    public double textWidth(String text) {
        return TextRenderer.get().getWidth(text);
    }

    public double textHeight() {
        return TextRenderer.get().getHeight();
    }

    public void addPostTask(Runnable runnable) {
        postTasks.add(runnable);
    }
}
