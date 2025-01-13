package nekiplay.protrainer.features.commands;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.orbit.EventHandler;
import nekiplay.Main;
import nekiplay.protrainer.ProTrainerAddon;
import nekiplay.protrainer.data.BlockData;
import nekiplay.protrainer.data.BlockDataAndPosition;
import nekiplay.protrainer.data.BlockPosition;
import nekiplay.protrainer.data.ProTrainerMap;
import nekiplay.protrainer.mixin.minecraft.PlayerMoveC2SPacketAccesor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TrainerCommand extends Command {
	public TrainerCommand() {
		super("protrainer", "Pro trainer command", "trainer");
	}

	public BlockPos pos1 = null;
	public BlockPos pos2 = null;
	public Gson gson = new Gson();
	public boolean started = false;
	public Vec3d start_position = new Vec3d(0, 0, 0);

	public List<BlockDataAndPosition> replaced = new ArrayList<>();
	public HashMap<BlockPosition, BlockData> map_blocks = new HashMap<>();

	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("pos1").executes(context -> {
			pos1 = mc.player.getBlockPos();
			info("Position 1 established");
			return SINGLE_SUCCESS;
		}));
		builder.then(literal("pos2").executes(context -> {
			pos2 = mc.player.getBlockPos();
			info("Position 2 established");
			return SINGLE_SUCCESS;
		}));
		builder.then(literal("start").then(argument("map", StringArgumentType.string()).executes(context -> {
			if (started) {
				error("Parkour has already begun");
				return SINGLE_SUCCESS;
			}
			if (mc.player.isOnGround()) {
				String map = context.getArgument("map", String.class);

				File dir = new File(MeteorClient.FOLDER, "protrainer");
				if (!dir.exists()) {
					dir.mkdir();
				}
				File dir2 = new File(dir, map + ".json");

				start_position = mc.player.getPos();

				new Thread(() -> {
					try {
						map_blocks.clear();
						BufferedReader reader = Files.newBufferedReader(Path.of(dir2.toURI()), StandardCharsets.UTF_8);
						try {
							String json = reader.lines().collect(Collectors.joining());
							int counter = 0;
							ProTrainerMap proTrainerMap = gson.fromJson(json, ProTrainerMap.class);
							for (BlockDataAndPosition data : proTrainerMap.blocks) {
								assert mc.world != null;
								BlockState state = Block.getStateFromRawId(data.blockId);
								mc.player.setPos(start_position.x, start_position.y, start_position.z);
								BlockPos pos = new BlockPos((int) (data.blockPosition.x + mc.player.getBlockX()), (int) (data.blockPosition.y + mc.player.getBlockY()), (int) (data.blockPosition.z + mc.player.getBlockZ()));

								BlockDataAndPosition blockData = new BlockDataAndPosition();
								blockData.blockPosition = new BlockPosition(pos);
								blockData.blockId = Block.getRawIdFromState(mc.world.getBlockState(pos));
								replaced.add(blockData);
								map_blocks.put(new BlockPosition(pos), new BlockData(data.blockId));

								BlockState original_state = mc.world.getBlockState(pos);
								if (original_state.getBlock() != state.getBlock()) {
									mc.execute(() -> mc.world.setBlockState(pos, state));
									if (counter >= ProTrainerAddon.getInstance().module.loadingSpeed.get()) {
										Thread.sleep(1);
										counter = 0;
									} else {
										counter++;
									}
								}
							}
							info("Successfully loaded");

						} catch (JsonSyntaxException e) {
							ProTrainerAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in custom block: " + e);
							error("Incorect json syntax");
						} catch (InterruptedException e) {
							ProTrainerAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in custom block: " + e);
							error("Interrupted error");
						}
					} catch (IOException e) {
						ProTrainerAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in custom block: " + e);
						error("IO error");
					}
					started = true;
				}).start();
				return SINGLE_SUCCESS;
			}
			else {
				error("You need to be on the ground");
				return SINGLE_SUCCESS;
			}
		})));
		builder.then(literal("stop").executes(context -> {
			if (!started) {
				warning("Parkour hasn't started");
				return SINGLE_SUCCESS;
			}
			new Thread(() -> {
				int counter = 0;
				map_blocks.clear();
				for (BlockDataAndPosition replace : replaced) {
					BlockState state = Block.getStateFromRawId(replace.blockId);
					BlockPos pos = new BlockPos((int) replace.blockPosition.x, (int) replace.blockPosition.y, (int) replace.blockPosition.z);
					mc.execute(() -> mc.world.setBlockState(pos, state));
					if (counter >= ProTrainerAddon.getInstance().module.loadingSpeed.get()) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						counter = 0;
					} else {
						counter++;
					}
				}
				mc.player.setPos(start_position.x, start_position.y, start_position.z);
				started = false;
				replaced.clear();
				info("Parkour has been stopped");
			}).start();
			return SINGLE_SUCCESS;
		}));
		builder.then(literal("save").then(argument("map", StringArgumentType.string()).executes(context -> {
			if (pos1 == null) {
				error("Position 1 not established");
				return SINGLE_SUCCESS;
			}
			if (pos2 == null) {
				error("Position 2 not established");
				return SINGLE_SUCCESS;
			}
			String map = context.getArgument("map", String.class);
			ProTrainerMap trainerMap = new ProTrainerMap();

			List<BlockPos> blocks = collectBlocksBetween(mc.world, pos1, pos2);
			List<BlockPos> sortedBlocks = new ArrayList<>();
			for (BlockPos pos : blocks) {
				sortedBlocks.add(pos.subtract(new Vec3i(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ())));
			}
			for (BlockPos sorted : sortedBlocks) {
				var unsorted = sorted.add(new Vec3i(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ()));
				BlockState state = mc.world.getBlockState(unsorted);
				BlockDataAndPosition blockData = new BlockDataAndPosition();
				blockData.blockId = Block.getRawIdFromState(state);
				blockData.blockPosition = new BlockPosition(sorted);
				trainerMap.blocks.add(blockData);
			}

			File dir = new File(MeteorClient.FOLDER, "protrainer");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File dir2 = new File(dir, map + ".json");
			if (!dir2.exists()) {
				try {
					dir2.createNewFile();
				} catch (IOException e) {
					ProTrainerAddon.LOG.error(e.getLocalizedMessage());
					error("Error creating file");
					return SINGLE_SUCCESS;
				}
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(dir2, false))) {
				writer.write(gson.toJson(trainerMap));
				info("Successfully saved");
			} catch (IOException e) {
				ProTrainerAddon.LOG.error(e.getLocalizedMessage());
				error("Error saving map");
			}
			return SINGLE_SUCCESS;
		})));
	}

	public List<BlockPos> collectBlocksBetween(World world, BlockPos start, BlockPos end) {
		List<BlockPos> blocks = new ArrayList<>();

		// Определяем границы диапазона
		int minX = Math.min(start.getX(), end.getX());
		int maxX = Math.max(start.getX(), end.getX());
		int minY = Math.min(start.getY(), end.getY());
		int maxY = Math.max(start.getY(), end.getY());
		int minZ = Math.min(start.getZ(), end.getZ());
		int maxZ = Math.max(start.getZ(), end.getZ());

		// Итерируем по всем блокам в пределах заданного диапазона
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					blocks.add(blockPos);
				}
			}
		}

		return blocks;
	}

	@EventHandler
	private void onBlockUpdate(BlockUpdateEvent event) {
		BlockPosition position = new BlockPosition(event.pos);
		BlockDataAndPosition position2 = new BlockDataAndPosition(Blocks.AIR.getDefaultState(), event.pos);
		if (map_blocks.containsKey(position) && started) {
			BlockData data = map_blocks.get(position);
			mc.world.setBlockState(event.pos, Block.getStateFromRawId(data.blockId));
		}
		if (started && replaced.contains(position2)) {
			int index = replaced.indexOf(position2);
			replaced.set(index, new BlockDataAndPosition(event.newState, event.pos));
		}
	}

	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket playerMoveC2SPacket) {
			if (started) {
				PlayerMoveC2SPacketAccesor accesor = (PlayerMoveC2SPacketAccesor) playerMoveC2SPacket;
				accesor.setX(start_position.x);
				accesor.setY(start_position.y);
				accesor.setZ(start_position.z);
				accesor.setOnGround(true);
			}
		}
	}
}
