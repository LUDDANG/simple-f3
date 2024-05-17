package kr.enak.minecraft.mods.fabric.simplef3.mixins;

import kr.enak.minecraft.mods.fabric.simplef3.client.SimpleF3Client;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenu extends Screen {
    protected MixinGameMenu(Text title) {
        super(title);
    }

    private static String getButtonStatusMessage() {
        return "좌표 간소화: " + (SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3 ? "켜짐" : "꺼짐");
    }

    @Inject(at = @At("TAIL"), method = "initWidgets")
    private void onInitWidgets(CallbackInfo ci) {
        addButtons();
    }

    private void addButtons() {
        List<ClickableWidget> buttons = Screens.getButtons(this);

        int buttonY = -1;
        int buttonI = -1;

        for (int i = 0; i < buttons.size(); i++) {
            ClickableWidget button = buttons.get(i);

            if (hasTrKey(button, "menu.sendFeedback")) {
                buttonY = button.getY();
                buttonI = i;

                button.visible = false;

                break;
            }
        }

        ClickableWidget button = ButtonWidget.builder(Text.literal(getButtonStatusMessage()), this::toggleButton)
                .dimensions(width / 2 - 102, buttonY, 98, 20).build();
        buttons.add(button);
    }

    private void toggleButton(ClickableWidget button) {
        SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3 = !SimpleF3Client.OPTIONS_SHOW_SIMPLE_F3;
        button.setMessage(Text.literal(getButtonStatusMessage()));
    }

    private static boolean hasTrKey(ClickableWidget button, String key) {
        String message = button.getMessage().getString();
        return message != null && message.equals(I18n.translate(key));
    }
}
