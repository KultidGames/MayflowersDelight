package net.lambdacomplex.mayflowersdelight.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentingBarrelScreen extends AbstractContainerScreen<FermentingBarrelMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MayflowersDelightMain.MODID,"textures/gui/fermenting_barrel_gui.png");
    public FermentingBarrelScreen(FermentingBarrelMenu menu, Inventory inv, Component component) {
        super(menu, inv, component);
    }
    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pPoseStack, x, y);
    }

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting()) {
            blit(pPoseStack, x + 47, y + 35, 175, 0, 14, menu.getScaledProgress()); //Where it should draw on the screen
            blit(pPoseStack, x + 83, y + 35, 175, 0, 14, menu.getScaledProgress()); //Where it should draw on the screen
            blit(pPoseStack, x + 119, y + 35, 175, 0, 14, menu.getScaledProgress()); //Where it should draw on the screen
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
