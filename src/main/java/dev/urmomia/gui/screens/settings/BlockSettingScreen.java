package dev.urmomia.gui.screens.settings;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.WItemWithLabel;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.settings.BlockSetting;
import dev.urmomia.utils.misc.Names;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class BlockSettingScreen extends WindowScreen {
    private final BlockSetting setting;

    private final WTextBox filter;
    private WTable table;

    private String filterText = "";

    public BlockSettingScreen(GuiTheme theme, BlockSetting setting) {
        super(theme, "Select blocks");

        this.setting = setting;

        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initWidgets();
        };

        table = add(theme.table()).expandX().widget();

        initWidgets();
    }

    private void initWidgets() {
        for (Block block : Registry.BLOCK) {
            if (setting.filter != null) {
                if (!setting.filter.test(block)) continue;
            }
            else {
                if (block == Blocks.AIR) continue;
            }

            if (skipValue(block)) continue;

            WItemWithLabel item = theme.itemWithLabel(block.asItem().getDefaultStack(), Names.get(block));
            if (!filterText.isEmpty()) {
                if (!StringUtils.containsIgnoreCase(item.getLabelText(), filterText)) continue;
            }
            table.add(item);

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(block);
                onClose();
            };

            table.row();
        }
    }

    protected boolean skipValue(Block value) {
        return Registry.BLOCK.getId(value).getPath().endsWith("_wall_banner");
    }
}