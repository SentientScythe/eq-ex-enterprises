package moze_intel.projecte.emc.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import net.neoforged.fml.loading.FMLPaths;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.MappingConfig;
import moze_intel.projecte.config.ProjectEConfig;

public class GraphExporter {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void exportEmcValues(Object2LongMap<ItemInfo> emc) {
		if (!MappingConfig.exportGraphs()) {
			return;
		}

		Path configDir = FMLPaths.CONFIGDIR.get().resolve("e3");
		try {
			Files.createDirectories(configDir);
			Path file = configDir.resolve("emc_values.json");
			
			JsonObject root = new JsonObject();
			for (Object2LongMap.Entry<ItemInfo> entry : emc.object2LongEntrySet()) {
				root.addProperty(entry.getKey().toString(), entry.getLongValue());
			}
			
			Files.writeString(file, GSON.toJson(root), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			PECore.debugLog("Exported emc_values.json");
		} catch (IOException e) {
			PECore.LOGGER.error("Failed to export emc_values.json", e);
		}
	}

	public static void exportRecipeInformation(Map<NormalizedSimpleStack, ?> graphData) {
		if (!MappingConfig.exportGraphs()) {
			return;
		}

		Path configDir = FMLPaths.CONFIGDIR.get().resolve("e3");
		try {
			Files.createDirectories(configDir);
			Path file = configDir.resolve("recipe_information.json");
			
			// For now, dump the graph mapping output or reasons
			// We can pass the raw data from SimpleGraphMapper
			JsonObject root = new JsonObject();
			
			for (Map.Entry<NormalizedSimpleStack, ?> entry : graphData.entrySet()) {
				root.addProperty(entry.getKey().toString(), String.valueOf(entry.getValue()));
			}
			
			Files.writeString(file, GSON.toJson(root), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			PECore.debugLog("Exported recipe_information.json");
		} catch (IOException e) {
			PECore.LOGGER.error("Failed to export recipe_information.json", e);
		}
	}
}
