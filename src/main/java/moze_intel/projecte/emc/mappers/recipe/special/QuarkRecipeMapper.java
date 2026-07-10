package moze_intel.projecte.emc.mappers.recipe.special;

import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper(priority = 1)
public class QuarkRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "QuarkRecipeMapper";
	}

	@Override
	public String getTranslationKey() {
		return "pe.emc.mapping.recipe.quark";
	}

	@Override
	public String getDescription() {
		return "Maps Quark's special crafting recipes.";
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
		return id != null && id.getNamespace().equals("quark");
	}
}
