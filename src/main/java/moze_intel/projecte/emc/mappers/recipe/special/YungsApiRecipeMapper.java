package moze_intel.projecte.emc.mappers.recipe.special;

import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper(priority = 1)
public class YungsApiRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "YungsApiRecipeMapper";
	}

	@Override
	public String getTranslationKey() {
		return "pe.emc.mapping.recipe.yungsapi";
	}

	@Override
	public String getDescription() {
		return "Maps YUNG's API special recipes.";
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
		return id != null && id.getNamespace().equals("yungsapi");
	}
}
