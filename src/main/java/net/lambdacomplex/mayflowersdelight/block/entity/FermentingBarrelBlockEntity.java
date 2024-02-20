package net.lambdacomplex.mayflowersdelight.block.entity;

import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.lambdacomplex.mayflowersdelight.item.custom.AlcoholicDrink;
import net.lambdacomplex.mayflowersdelight.recipe.BrewingRecipeList;
import net.lambdacomplex.mayflowersdelight.screen.FermentingBarrelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.FarmersDelight;

public class FermentingBarrelBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 0; // This will be dynamically updated

    public FermentingBarrelBlockEntity(BlockPos blockPos, BlockState state) {
        super(ModBlockEntities.FERMENTING_BARREL.get(), blockPos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> FermentingBarrelBlockEntity.this.progress;
                    case 1 -> FermentingBarrelBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FermentingBarrelBlockEntity.this.progress = value;
                    case 1 -> FermentingBarrelBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 5;
            } //TODO CHANGE ME W SLOT #
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Fermenting Barrel"); //TODO Change to Translatable Text
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FermentingBarrelMenu(id, inv, this, this.data);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap ==  ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    private static void craftItem(FermentingBarrelBlockEntity entity, ItemStack output, boolean consumeEnhancer) {
        if (consumeEnhancer) {
            // Consume one redstone from any of the enhancement slots (1, 2, or 3)
            for (int slot = 1; slot <= 3; slot++) {
                if (entity.itemHandler.getStackInSlot(slot).getItem() == Items.REDSTONE) {
                    entity.itemHandler.extractItem(slot, 1, false); // Consume 1 redstone
                    break; // Stop after consuming from the first matching slot
                }
            }
        }
        entity.itemHandler.setStackInSlot(4, output); // Update output in slot 4
        entity.resetProgress();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentingBarrelBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        ItemStack inputStack = entity.itemHandler.getStackInSlot(0);
        ItemStack outputStackInSlot = entity.itemHandler.getStackInSlot(4);
        boolean enhanced = false;


        // Check if the input slot is empty and reset progress and maxProgress
        if (inputStack.isEmpty()) {
            if (entity.progress != 0 || entity.maxProgress != 0) {
                entity.progress = 0;
                entity.maxProgress = 0; // Reset to a default or idle value
                setChanged(level, pos, state);
            }
            return; // Skip crafting logic if there's no input
        }

        // Check for enhancement
        for (int slot = 1; slot <= 3; slot++) {
            if (entity.itemHandler.getStackInSlot(slot).getItem() == Items.REDSTONE) {
                enhanced = true; // Found redstone for enhancement
                break;
            }
        }

        for (BrewingRecipeList.Recipe recipe : BrewingRecipeList.RECIPES) {
            if (inputStack.getItem() == recipe.getInput().getItem() && inputStack.getCount() >= recipe.getInput().getCount()) {
                ItemStack recipeOutput = recipe.getOutput().copy();
                if (enhanced && recipeOutput.getItem() instanceof AlcoholicDrink) {
                    recipeOutput.getOrCreateTag().putBoolean("Enhanced", true);
                }

                // Check if crafting can proceed based on output slot condition
                if (outputStackInSlot.isEmpty() || (outputStackInSlot.is(recipeOutput.getItem()) && ItemStack.tagMatches(outputStackInSlot, recipeOutput) && outputStackInSlot.getCount() + recipeOutput.getCount() <= outputStackInSlot.getMaxStackSize())) {
                    if (entity.progress == 0) {
                        entity.maxProgress = recipe.getMaxProgress();
                    }
                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= entity.maxProgress) {
                        entity.itemHandler.extractItem(0, recipe.getInput().getCount(), false); // Consume input
                        if (!outputStackInSlot.isEmpty()) {
                            recipeOutput.setCount(outputStackInSlot.getCount() + recipeOutput.getCount()); // Add to existing output stack
                        }
                        craftItem(entity, recipeOutput, enhanced);
                    }
                    return; // Found and processed a recipe, so exit
                } else {
                    // Output slot condition not met, halt progress
                    entity.resetProgress();
                    return;
                }
            }
        }

        // No valid recipe found or in progress, reset progress
        entity.resetProgress();
    }


    private void resetProgress() {
        this.progress = 0;
        setChanged();
    }
}