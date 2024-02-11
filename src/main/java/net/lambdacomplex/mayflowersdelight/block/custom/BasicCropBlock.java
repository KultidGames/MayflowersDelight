package net.lambdacomplex.mayflowersdelight.block.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BasicCropBlock extends CropBlock {
    public int MAX_AGE;
    public static IntegerProperty AGE = IntegerProperty.create("age", 0, CropBlock.MAX_AGE);
    public BasicCropBlock(Properties properties, int MAX_AGE) {
        super(properties);
        this.MAX_AGE = MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId(){
        return null;
    }

    @Override
    public IntegerProperty getAgeProperty(){
        return AGE;
    }
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
