package net.lambdacomplex.mayflowersdelight.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.lambdacomplex.mayflowersdelight.block.custom.DryingTableBlock;
import net.lambdacomplex.mayflowersdelight.block.entity.DryingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class DryingTableBlockEntityRenderer implements BlockEntityRenderer<DryingTableBlockEntity> {
    public DryingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(DryingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack inputStack = blockEntity.getRenderInputStack(); // Assuming this gets the input stack
        int inputCount = inputStack.getCount();

        for (int i = 0; i < Math.min(inputCount, 7); i++) {
            poseStack.pushPose();
            // Adjustments for stacking and positioning
            poseStack.translate(0.5f, 0.76f + i * 0.04f, 0.2f);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));
            // Slightly rotate each item in the stack
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(i * 8));
            // Scale the item
            poseStack.scale(0.55f, 0.55f, 0.55f);
            // Render the item
            itemRenderer.renderStatic(inputStack, ItemTransforms.TransformType.GUI, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, i);
            poseStack.popPose();
        }

        ItemStack outputStack = blockEntity.getRenderOutputStack(); // Assuming you have a method to get the output stack
        int outputCount = outputStack.getCount();

        for (int i = 0; i < Math.min(outputCount, 7); i++) {
            poseStack.pushPose();
            // Adjustments for stacking and positioning for the output stack
            poseStack.translate(0.5f, 0.76f + i * 0.04f, 0.8f);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));
            // Slightly rotate each item in the output stack
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(i * 8));
            // Scale the item
            poseStack.scale(0.55f, 0.55f, 0.55f);
            // Render the item
            itemRenderer.renderStatic(outputStack, ItemTransforms.TransformType.GUI, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, i);
            poseStack.popPose();
        }
    }


    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
