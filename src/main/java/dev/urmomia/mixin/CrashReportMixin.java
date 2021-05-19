package dev.urmomia.mixin;

import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.modules.Category;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.utils.Utils;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "addStackTrace", at = @At("TAIL"))
    private void onAddStackTrace(StringBuilder sb, CallbackInfo info) {
        if (Modules.get() != null) {
            final List<String> bub = new ArrayList<>(12);
            bub.add("you must've did something terrible didn't you?");
            bub.add("did you remember to star Urmomia's github?");
            bub.add("ur mom fat!");
            bub.add("urfamilia");
            bub.add("urdadia");
            bub.add("bub plump floppa");
            bub.add("pee fart butt cheese cake shrimp beans");
            bub.add("aimes tu le fromage");
            bub.add("call me honsda-ddy");
            bub.add("hi there");
            bub.add("snale is cool!");
            bub.add("rrrrrr");


            int i;
            
            i = Utils.random(0, bub.size());

            sb.append("\n\n");
            sb.append("-- Urmomia Client --\n");
            sb.append("Version: ").append(Config.version.getOriginalString()).append("\n");

            sb.append(bub.get(i)).append("\n");

            for (Category category : Modules.loopCategories()) {
                List<Module> modules = Modules.get().getGroup(category);
                boolean active = false;
                for (Module module : modules) {
                    if (module instanceof Module && module.isActive()) {
                        active = true;
                        break;
                    }
                }

                if (active) {
                    sb.append("\n");
                    sb.append("[").append(category).append("]:").append("\n");

                    for (Module module : modules) {
                        if (module instanceof Module && module.isActive()) {
                            sb.append(module.title).append(" (").append(module.name).append(")\n");
                        }
                    }
                }
            }
        }
    }
}
