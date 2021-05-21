package dev.urmomia.gui.themes.urmomia;

import dev.urmomia.gui.DefaultSettingsWidgetFactory;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.gui.renderer.packer.GuiTexture;
import dev.urmomia.gui.themes.urmomia.widgets.*;
import dev.urmomia.gui.themes.urmomia.widgets.input.WThemeDropdown;
import dev.urmomia.gui.themes.urmomia.widgets.input.WThemeSlider;
import dev.urmomia.gui.themes.urmomia.widgets.input.WThemeTextBox;
import dev.urmomia.gui.themes.urmomia.widgets.pressable.*;
import dev.urmomia.gui.utils.AlignmentX;
import dev.urmomia.gui.utils.CharFilter;
import dev.urmomia.gui.widgets.*;
import dev.urmomia.gui.widgets.containers.WSection;
import dev.urmomia.gui.widgets.containers.WView;
import dev.urmomia.gui.widgets.containers.WWindow;
import dev.urmomia.gui.widgets.input.WDropdown;
import dev.urmomia.gui.widgets.input.WSlider;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.*;
import dev.urmomia.rendering.text.TextRenderer;
import dev.urmomia.settings.*;
import dev.urmomia.systems.accounts.Account;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.render.color.SettingColor;

import static dev.urmomia.utils.Utils.mc;

public class ThemeGuiTheme extends GuiTheme {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");
    private final SettingGroup sgTextColors = settings.createGroup("Text");
    private final SettingGroup sgBackgroundColors = settings.createGroup("Background");
    private final SettingGroup sgOutline = settings.createGroup("Outline");
    private final SettingGroup sgSeparator = settings.createGroup("Separator");
    private final SettingGroup sgScrollbar = settings.createGroup("Scrollbar");
    private final SettingGroup sgSlider = settings.createGroup("Slider");

    // General

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of the GUI.")
            .defaultValue(0.8)
            .min(0.75)
            .sliderMin(0.75)
            .sliderMax(4)
            .onSliderRelease()
            .onChanged(aDouble -> {
                if (mc.currentScreen instanceof WidgetScreen) ((WidgetScreen) mc.currentScreen).invalidate();
            })
            .build()
    );

    public final Setting<AlignmentX> moduleAlignment = sgGeneral.add(new EnumSetting.Builder<AlignmentX>()
            .name("module-alignment")
            .description("How module titles are aligned.")
            .defaultValue(AlignmentX.Center)
            .build()
    );

    public final Setting<Boolean> categoryIcons = sgGeneral.add(new BoolSetting.Builder()
            .name("category-icons")
            .description("Adds item icons to module categories.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> blur = sgGeneral.add(new BoolSetting.Builder()
            .name("blur")
            .description("Apply blur behind the GUI.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> gradient = sgGeneral.add(new BoolSetting.Builder()
            .name("gradient-header")
            .description("Make the header have a gradient.")
            .defaultValue(true)
            .build()
    );

    public final Setting<plump> borderGui = sgGeneral.add(new EnumSetting.Builder<plump>()
            .name("window-border")
            .description("Type of the border of the windows.")
            .defaultValue(plump.Horizontal)
            .build()
    );

    // Colors

    public final Setting<SettingColor> accentColor = color("accent", "Main color of the GUI.", new SettingColor(135, 0, 255));
    public final Setting<SettingColor> accentColor2 = color("second-accent", "Secondary color of the GUI.", new SettingColor(175, 0, 255));
    public final Setting<SettingColor> checkboxColor = color("checkbox", "Color of checkbox.", new SettingColor(135, 0, 255));
    public final Setting<SettingColor> plusColor = color("plus", "Color of plus button.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> minusColor = color("minus", "Color of minus button.", new SettingColor(255, 255, 255));

    // Text

    public final Setting<SettingColor> textColor = color(sgTextColors, "text", "Color of text.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> textSecondaryColor = color(sgTextColors, "text-secondary-text", "Color of secondary text.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> titleTextColor = color(sgTextColors, "title-text", "Color of title text.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> loggedInColor = color(sgTextColors, "logged-in-text", "Color of logged in account name.", new SettingColor(45, 225, 45));

    // Background

    public final ThreeStateColorSetting backgroundColor = new ThreeStateColorSetting(
            sgBackgroundColors,
            "background",
            new SettingColor(20, 20, 20, 200),
            new SettingColor(30, 30, 30, 200),
            new SettingColor(40, 40, 40, 200)
    );

    public final ThreeStateColorSetting topbarColor = new ThreeStateColorSetting(
        sgBackgroundColors,
        "top-bar-background",
        new SettingColor(25, 0, 56, 200),
        new SettingColor(35, 0, 66, 200),
        new SettingColor(45, 0, 76, 200)
    );

    public final Setting<SettingColor> moduleBackground = color(sgBackgroundColors, "module-background", "Color of module background when active.", new SettingColor(35, 0, 66));

    // Outline

    public final ThreeStateColorSetting outlineColor = new ThreeStateColorSetting(
            sgOutline,
            "outline",
            new SettingColor(0, 0, 0),
            new SettingColor(10, 10, 10),
            new SettingColor(20, 20, 20)
    );

    // Separator

    public final Setting<SettingColor> separatorText = color(sgSeparator, "separator-text", "Color of separator text", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> separatorCenter = color(sgSeparator, "separator-center", "Center color of separators.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> separatorEdges = color(sgSeparator, "separator-edges", "Color of separator edges.", new SettingColor(225, 225, 225, 150));

    // Scrollbar

    public final ThreeStateColorSetting scrollbarColor = new ThreeStateColorSetting(
            sgScrollbar,
            "Scrollbar",
            new SettingColor(30, 30, 30, 200),
            new SettingColor(40, 40, 40, 200),
            new SettingColor(50, 50, 50, 200)
    );

    // Slider

    public final ThreeStateColorSetting sliderHandle = new ThreeStateColorSetting(
            sgSlider,
            "slider-handle",
            new SettingColor(0, 255, 180),
            new SettingColor(0, 240, 165),
            new SettingColor(0, 225, 150)
    );

    public final Setting<SettingColor> sliderLeft = color(sgSlider, "slider-left", "Color of slider left part.", new SettingColor(0, 150, 80));
    public final Setting<SettingColor> sliderRight = color(sgSlider, "slider-right", "Color of slider right part.", new SettingColor(50, 50, 50));

    public ThemeGuiTheme() {
        super("Urmomia");

        settingsFactory = new DefaultSettingsWidgetFactory(this);
    }

    private Setting<SettingColor> color(SettingGroup group, String name, String description, SettingColor color) {
        return group.add(new ColorSetting.Builder()
                .name(name + "-color")
                .description(description)
                .defaultValue(color)
                .build());
    }
    private Setting<SettingColor> color(String name, String description, SettingColor color) {
        return color(sgColors, name, description, color);
    }

    // Widgets

    @Override
    public WWindow window(String title) {
        return w(new WThemeWindow(title));
    }

    @Override
    public WLabel label(String text, boolean title, double maxWidth) {
        if (maxWidth == 0) return w(new WThemeLabel(text, title));
        return w(new WThemeMultiLabel(text, title, maxWidth));
    }

    @Override
    public WHorizontalSeparator horizontalSeparator(String text) {
        return w(new WThemeHorizontalSeparator(text));
    }

    @Override
    public WVerticalSeparator verticalSeparator() {
        return w(new WThemeVerticalSeparator());
    }

    @Override
    protected WButton button(String text, GuiTexture texture) {
        return w(new WThemeButton(text, texture));
    }

    @Override
    public WMinus minus() {
        return w(new WThemeMinus());
    }

    @Override
    public WPlus plus() {
        return w(new WThemePlus());
    }

    @Override
    public WCheckbox checkbox(boolean checked) {
        return w(new WThemeCheckbox(checked));
    }

    @Override
    public WSlider slider(double value, double min, double max) {
        return w(new WThemeSlider(value, min, max));
    }

    @Override
    public WTextBox textBox(String text, CharFilter filter) {
        return w(new WThemeTextBox(text, filter));
    }

    @Override
    public <T> WDropdown<T> dropdown(T[] values, T value) {
        return w(new WThemeDropdown<>(values, value));
    }

    @Override
    public WTriangle triangle() {
        return w(new WThemeTriangle());
    }

    @Override
    public WMenu menu(boolean open) {
        return w(new WThemeMenu(open));
    }

    @Override
    public WTooltip tooltip(String text) {
        return w(new WThemeTooltip(text));
    }

    @Override
    public WView view() {
        return w(new WThemeView());
    }

    @Override
    public WSection section(String title, boolean expanded, WWidget headerWidget) {
        return w(new WThemeSection(title, expanded, headerWidget));
    }

    @Override
    public WAccount account(WidgetScreen screen, Account<?> account) {
        return w(new WThemeAccount(screen, account));
    }

    @Override
    public WWidget module(Module module) {
        return w(new WThemeModule(module));
    }

    @Override
    public WQuad quad(Color color) {
        return w(new WThemeQuad(color));
    }

    @Override
    public WTopBar topBar() {
        return w(new WThemeTopBar());
    }

    // Colors

    @Override
    public Color textColor() {
        return textColor.get();
    }

    @Override
    public Color textSecondaryColor() {
        return textSecondaryColor.get();
    }

    // Other

    @Override
    public TextRenderer textRenderer() {
        return TextRenderer.get();
    }

    @Override
    public double scale(double value) {
        return value * scale.get();
    }

    @Override
    public boolean categoryIcons() {
        return categoryIcons.get();
    }

    @Override
    public boolean blur() {
        return blur.get();
    }

    public class ThreeStateColorSetting {
        private final Setting<SettingColor> normal, hovered, pressed;

        public ThreeStateColorSetting(SettingGroup group, String name, SettingColor c1, SettingColor c2, SettingColor c3) {
            normal = color(group, name, "Color of " + name + ".", c1);
            hovered = color(group, "hovered-" + name, "Color of " + name + " when hovered.", c2);
            pressed = color(group, "pressed-" + name, "Color of " + name + " when pressed.", c3);
        }

        public SettingColor get() {
            return normal.get();
        }

        public SettingColor get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
            if (pressed) return this.pressed.get();
            return (hovered && (bypassDisableHoverColor || !disableHoverColor)) ? this.hovered.get() : this.normal.get();
        }

        public SettingColor get(boolean pressed, boolean hovered) {
            return get(pressed, hovered, false);
        }
    }

    public enum plump {
        None,
        Horizontal,
        Vertical,
        Diagonal,
        Solid
    }
}
