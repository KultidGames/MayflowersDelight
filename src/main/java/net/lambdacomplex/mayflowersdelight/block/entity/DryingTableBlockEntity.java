package net.lambdacomplex.mayflowersdelight.block.entity;

import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.lambdacomplex.mayflowersdelight.screen.DryingTableMenu;
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

public class DryingTableBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 50; //Change me for length of crafting

    public DryingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_TABLE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> DryingTableBlockEntity.this.progress;
                    case 1 -> DryingTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DryingTableBlockEntity.this.progress = value;
                    case 1 -> DryingTableBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Drying Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new DryingTableMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
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
        nbt.putInt("drying_table.progress", this.progress);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("drying_table.progress");
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DryingTableBlockEntity pEntity) {
        if (level.isClientSide()) {
            return;
        }
        // Debug: Check if the tick method is being called.
        System.out.println("Tick called for DryingTableBlockEntity at " + pos);

        if (hasRecipe(pEntity)) {
            // Debug: Check if the recipe is detected.
            System.out.println("Recipe detected, progressing. Current progress: " + pEntity.progress);

            pEntity.progress++;
            setChanged(level, pos, state);

            if (pEntity.progress >= pEntity.maxProgress) {
                // Debug: Check if crafting is triggered.
                System.out.println("Crafting triggered.");
                craftItem(pEntity);
            }
        } else {
            if (pEntity.progress > 0) {
                // Debug: Check if the progress is reset when it should not.
                System.out.println("Progress reset unexpectedly.");
            }
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(DryingTableBlockEntity pEntity) {
        if (hasRecipe(pEntity)) {
            // Debug: Log the state before crafting.
            System.out.println("Crafting item. Current output before crafting: " + pEntity.itemHandler.getStackInSlot(1).getCount());

            pEntity.itemHandler.extractItem(0, 1, false);

            ItemStack outputStack = pEntity.itemHandler.getStackInSlot(1);
            if (!outputStack.isEmpty()) {
                outputStack.grow(1);
            } else {
                pEntity.itemHandler.setStackInSlot(1, new ItemStack(Items.APPLE, 1));
            }

            // Debug: Log the state after crafting.
            System.out.println("Crafting completed. New output count: " + pEntity.itemHandler.getStackInSlot(1).getCount());

            pEntity.resetProgress();
        } else {
            // Debug: If crafting fails due to recipe check
            System.out.println("Crafting failed. Recipe check did not pass.");
        }
    }



    private static boolean hasRecipe(DryingTableBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        boolean hasCraftableItemInFirstSlot = entity.itemHandler.getStackInSlot(0).getItem() == ModItems.BARLEY.get();
        System.out.println("Checking recipe conditions.");
        return hasCraftableItemInFirstSlot && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, new ItemStack(ModItems.LOGO.get(), 1));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return inventory.getItem(1).getItem() == stack.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }




}
