package net.lambdacomplex.mayflowersdelight.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Blocks;

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
            long ticks = (System.currentTimeMillis() / 50) % 40; // Loop every 2 seconds (40 ticks).
            float progress = (float)ticks / 40; // Normalize progress to [0,1].
            int progressHeight = (int)(35 * progress); // Scale progress to the height of the arrow.

            // Calculate startY to start drawing from the bottom.
            int startY = y + 28 + (35 - progressHeight);

            // Draw the progress arrow from bottom to top by adjusting the texture Y offset and the height.
            blit(pPoseStack, x + 47, startY, 175, 35 - progressHeight, 14, progressHeight);
            blit(pPoseStack, x + 83, startY, 175, 35 - progressHeight, 14, progressHeight);
            blit(pPoseStack, x + 119, startY, 175, 35 - progressHeight, 14, progressHeight);
        }
    }



    @Override
    protected void renderLabels(PoseStack pPoseStack, int mouseX, int mouseY) {
        // Manually draw the container name to keep it while not drawing the "Inventory" text
        this.font.draw(pPoseStack, this.title, (float)(this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
    }

    private void drawMaxProgressTime(PoseStack pPoseStack) {
        int currentProgress = this.menu.getProgress();
        int maxProgressTicks = this.menu.getMaxProgress();
        int remainingTicks = maxProgressTicks - currentProgress;
        String timeString = convertTicksToTimeString(remainingTicks);
        int x = this.leftPos + 142; // Corrected to be GUI-relative
        int y = this.topPos + 6; // Corrected to be GUI-relative
        this.font.draw(pPoseStack, timeString, x, y, 0xFF353535);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        this.renderTooltip(pPoseStack, mouseX, mouseY);
        drawMaxProgressTime(pPoseStack);
    }

    private String convertTicksToTimeString(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}