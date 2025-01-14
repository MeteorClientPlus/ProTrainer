package nekiplay.protrainer.features.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.List;

public class ProTrainerModule extends Module {
	public ProTrainerModule() {
		super(Categories.World, "pro-trainer", "Settings for trainer command");
	}

	private final SettingGroup defaultGroup = settings.getDefaultGroup();
	private final SettingGroup spawnpointsGroup = settings.createGroup("Spawnpoints");
	private final SettingGroup respawnGroup = settings.createGroup("Respawning");

	public final Setting<Integer> loadingSpeed = defaultGroup.add(new IntSetting.Builder()
		.name("loading-speed")
		.description("World loading speed in blocks.")
		.defaultValue(64)
		.min(1)
		.max(512)
		.sliderMin(1)
		.sliderMax(512)
		.build()
	);
	public final Setting<List<Block>> respawnBlocks = respawnGroup.add(new BlockListSetting.Builder()
		.name("respawn-blocks")
		.defaultValue(List.of(Blocks.LAVA, Blocks.MAGMA_BLOCK))
		.build()
	);
	public final Setting<List<Block>> checkPointsBlocks = respawnGroup.add(new BlockListSetting.Builder()
		.name("checkpoint-blocks")
		.defaultValue(List.of(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE))
		.build()
	);

	public final Setting<SettingColor> spawnpointTextColor = spawnpointsGroup.add(new ColorSetting.Builder()
		.name("spawnpoint-text-color")
		.defaultValue(new SettingColor(255, 0, 0, 255))
		.build()
	);

	public final Setting<Double> spawnpointTextRenderDistance = spawnpointsGroup.add(new DoubleSetting.Builder()
		.name("spawnpoint-text-render-distance")
		.defaultValue(32)
		.max(64)
		.sliderRange(0, 64)
		.build()
	);
}
