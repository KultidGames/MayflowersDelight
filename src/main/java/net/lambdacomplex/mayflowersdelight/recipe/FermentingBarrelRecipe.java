package net.lambdacomplex.mayflowersdelight.recipe;

import com.google.gson.JsonObject;
import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class FermentingBarrelRecipe extends CustomRecipe {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public FermentingBarrelRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        super(id);
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        System.out.println("ALARM! FermentingBarrelRecipe created with id: " + id);
    }


    @Override
    public NonNullList<Ingredient> getIngredients(){
        return recipeItems;
    }


    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            System.out.println("ALARM! isClientSide!!");
            return false;
        }
        System.out.println("ALARM! Checking if recipe matches...");
        boolean hasHoney = false;
        boolean hasFlower = false;
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack = pContainer.getItem(i);
            System.out.println("ALARM! Checking slot " + i + ": " + itemstack);
            if (itemstack.is(Items.HONEY_BOTTLE)) {
                if (hasHoney) {
                    System.out.println("ALARM! Already has honey, recipe does not match.");
                    return false; // Ensure only one honey bottle
                }
                hasHoney = true;
                System.out.println("ALARM! Found honey bottle.");
            } else if (!itemstack.isEmpty() && itemstack.is(Items.APPLE)) {
                hasFlower = true;
                System.out.println("ALARM! Found flower.");
            } else if (!itemstack.isEmpty()) {
                System.out.println("ALARM! Found invalid item, recipe does not match.");
                return false; // No other items allowed
            }
        }
        boolean matches = hasHoney && hasFlower;
        System.out.println("ALARM! Recipe match status: " + matches);
        return matches; // Must have both honey and a flower
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        System.out.println("ALARM! Assembling the recipe...");
        ItemStack mead = new ItemStack(ModItems.MEAD.get()); // Change to your Mead item
     //   PotionUtils.setPotion(mead, Potions.WATER); // Start with water potion as base

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemstack = container.getItem(i);
            if (!itemstack.isEmpty() && itemstack.is(Items.APPLE)) {
                // Here, you would add effects based on the flowers found
                // This is a placeholder for adding an effect; adjust according to your needs
                //    PotionUtils.addPotionEffect(mead, new MobEffectInstance(itemstack.getItem().getRegistryName().getEffect(), 100, 0));
                System.out.println("ALARM! Adding effects based on flower: " + itemstack);
            }
        }
        return mead;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true; //Change Me
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY; // Not used in dynamic recipes
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FermentingBarrelRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }



    public static class Serializer implements RecipeSerializer<FermentingBarrelRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private Serializer() {}

        @Override
        public FermentingBarrelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Return a dummy recipe that does nothing or has placeholder values.
            // This avoids UnsupportedOperationException and allows the game to proceed without actually using this method for your recipe.
            NonNullList<Ingredient> dummyIngredients = NonNullList.create();
            ItemStack dummyOutput = ItemStack.EMPTY; // Use a meaningful output if necessary.
            return new FermentingBarrelRecipe(recipeId, dummyOutput, dummyIngredients);
        }


        @Override
        public FermentingBarrelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            // Read the recipe inputs and output from the network buffer.
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack output = buffer.readItem();
            // Construct a new FermentingBarrelRecipe instance.
            return new FermentingBarrelRecipe(recipeId, output, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FermentingBarrelRecipe recipe) {
            // Write the recipe inputs and output to the network buffer.
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItemStack(recipe.getResultItem(), false);
        }
    }

}

