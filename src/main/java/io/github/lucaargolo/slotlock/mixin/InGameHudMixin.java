package io.github.lucaargolo.slotlock.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lucaargolo.slotlock.Slotlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;

    private static final Identifier SLOT_LOCK_TEXTURE = new Identifier(Slotlock.MOD_ID, "textures/gui/lock_overlay.png");
    private MatrixStack matrices;
    private int slotIndex = 0;

    @Inject(at = @At("HEAD"), method = "renderHotbar")
    public void renderHotbar(float f, MatrixStack matrixStack, CallbackInfo info) {
        matrices = matrixStack;
        slotIndex = 0;
    }

    @Inject(at = @At("HEAD"), method = "renderHotbarItem")
    public void renderHotbarItem(int i, int j, float tickDelta, PlayerEntity player, ItemStack stack, int k, CallbackInfo info) {
        if (player.getInventory().getStack(slotIndex).isEmpty()) Slotlock.unlockSlot(slotIndex);
        if(Slotlock.isLocked(slotIndex)) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, SLOT_LOCK_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexture(matrices, i, j, 0, 0, 16, 16);
        }
        slotIndex++;
    }

}
