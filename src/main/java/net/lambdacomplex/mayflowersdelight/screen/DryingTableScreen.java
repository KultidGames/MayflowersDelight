package net.lambdacomplex.mayflowersdelight.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DryingTableScreen extends AbstractContainerScreen<DryingTableMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MayflowersDelightMain.MODID,"textures/gui/drying_table_gui.png");
    public DryingTableScreen(DryingTableMenu menu, Inventory inv, Component component) {
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
            blit(pPoseStack, x + 63, y + 25, 174, 0, 50, menu.getScaledProgress()); //Where it should draw on the screen
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
