package kr.enak.minecraft.mods.fabric.simplef3.mixins;

import kr.enak.minecraft.mods.fabric.simplef3.client.SimpleF3Client;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugScreen {
    @Shadow protected abstract List<String> getLeftText();

    @Shadow protected abstract void drawText(DrawContext context, List<String> text, boolean left);

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    private void overrideLeftText(CallbackInfoReturnable<List<String>> cir) {
        if (!SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3) return;

        List<String> output = new ArrayList<>(cir.getReturnValue());
        List<String> whitelist = List.of(
                "XYZ:",
                "Biome:",
                "Facing:"
        );

        output.removeIf(str -> whitelist.stream().noneMatch(str::startsWith));

        for (int i = 0; i < output.size(); i++) {
            String str = output.get(i);

            if (str.startsWith("Facing:")) {
                output.set(i,
                        (str.substring(0, str.indexOf(") (")) + ")")
                                .replace("Towards ", "")
                                .replace("positive ", "+")
                                .replace("negative ", "-")
                );
            }
        }

        cir.setReturnValue(output);
    }

    @Inject(method = "drawLeftText", at = @At("HEAD"), cancellable = true)
    private void drawLeftText(DrawContext context, CallbackInfo ci) {
        if (!SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3) return;

        ci.cancel();
        List<String> list = getLeftText();
        this.drawText(context, list, true);
    }

    @Inject(method = "getRightText", at = @At("RETURN"), cancellable = true)
    private void overrideRightText(CallbackInfoReturnable<List<String>> cir) {
        if (!SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3) return;

        cir.setReturnValue(List.of());
    }
}
