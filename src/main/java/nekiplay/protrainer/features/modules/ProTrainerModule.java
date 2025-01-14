package nekiplay.protrainer.features.modules;

import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.List;

public class ProTrainerModule extends Module {
	public ProTrainerModule() {
		super(Categories.World, "pro-trainer", "Settings for trainer command");
	}

	private final SettingGroup defaultGroup = settings.getDefaultGroup();
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
		.description("The blocks you don't want to mine.")
		.defaultValue(List.of(Blocks.LAVA))
		.build()
	);
}
