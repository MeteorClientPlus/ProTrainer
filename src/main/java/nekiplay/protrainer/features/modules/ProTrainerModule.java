package nekiplay.protrainer.features.modules;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ProTrainerModule extends Module {
	public ProTrainerModule() {
		super(Categories.World, "pro-trainer", "Settings for trainer command");
	}

	private final SettingGroup defaultGroup = settings.getDefaultGroup();

	public final Setting<Integer> loadingSpeed = defaultGroup.add(new IntSetting.Builder()
		.name("loading-speed")
		.description("World loading speed in blocks.")
		.defaultValue(8)
		.min(1)
		.max(64)
		.sliderMin(1)
		.sliderMax(64)
		.build()
	);

}
