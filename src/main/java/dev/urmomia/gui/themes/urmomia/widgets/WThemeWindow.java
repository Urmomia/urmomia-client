package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.containers.WWindow;

public class WThemeWindow extends WWindow implements ThemeWidget {
    private boolean hh;
    private boolean forcehh;

    public WThemeWindow(String title) {
        super(title);
    }

    @Override
    protected WHeader header() {
        return new WThemeHeader();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0) {
            renderer.quad(x, y + header.height, width, height - header.height, theme().backgroundColor.get());
            //Border
            switch(theme().borderGui.get()) {
                case None:
                hh = false;
                forcehh = false;
                break;
                case Horizontal:
                renderer.horizontalGradientGuiBorder(x, y + header.height, width, height - header.height, theme().accentColor.get(), theme().accentColor2.get());
                hh = false;
                forcehh = true;
                break;
                case Vertical:
                renderer.verticalGradientGuiBorder(x, y + header.height, width, height - header.height, theme().accentColor.get(), theme().accentColor2.get());
                hh = true;
                forcehh = false;
                break;
                case Diagonal:
                renderer.diagonalGradientGuiBorder(x, y + header.height, width, height - header.height, theme().accentColor.get(), theme().accentColor2.get());
                hh = true;
                forcehh = false;
                case Solid:
                renderer.guiBorder(x, y + header.height, width, height - header.height, theme().accentColor.get());
                hh = true;
                forcehh = false;
                break;
            }
            
        }
    }

    private class WThemeHeader extends WHeader {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!(theme().gradient.get()) || hh && theme().gradient.get()) renderer.quad(this, theme().accentColor.get());
            if (!(hh) && theme().gradient.get()) renderer.horizontalGradientQuad(this, theme().accentColor.get(), theme().accentColor2.get());
            if (forcehh && !(theme().gradient.get())) renderer.horizontalGradientQuad(this, theme().accentColor.get(), theme().accentColor2.get());
        }
    }
}
