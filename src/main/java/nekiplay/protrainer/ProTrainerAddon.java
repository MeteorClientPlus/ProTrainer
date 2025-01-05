package nekiplay.protrainer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import nekiplay.protrainer.features.commands.TrainerCommand;
import net.fabricmc.loader.api.FabricLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nekiplay.Main.METEOR_LOGPREFIX;

public class ProTrainerAddon extends MeteorAddon {
	private static ProTrainerAddon instance;

	public static final Logger LOG = LoggerFactory.getLogger(ProTrainerAddon.class);

	public static ProTrainerAddon getInstance() {
		return instance;
	}

	@Override
	public void onInitialize() {
		instance = this;

		LOG.info("Initializing...");


		TrainerCommand command = new TrainerCommand();
		Commands.add(command);
		MeteorClient.EVENT_BUS.subscribe(command);

		LOG.info("Initializing done");

	}

	@Override
	public void onRegisterCategories() {

	}

	@Override
	public String getWebsite() {
		return "https://meteorclientplus.github.io";
	}

	@Override
	public GithubRepo getRepo() {
		return new GithubRepo("MeteorClientPlus", "ProTrainer",  "1.21.1", null);
	}

	@Override
	public String getCommit() {
		String commit = FabricLoader
			.getInstance()
			.getModContainer("protrainer")
			.get().getMetadata()
			.getCustomValue("github:sha")
			.getAsString();
		return commit.isEmpty() ? null : commit.trim();
	}

	@Override
	public String getPackage() {
		return "nekiplay.protrainer";
	}
}
