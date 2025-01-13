package nekiplay.protrainer.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockData {
	public int blockId = 0;

	public BlockData() {}

	public BlockData(int rawId) {
		this.blockId = rawId;
	}

	public BlockState toBlockState() {
		return Block.getStateFromRawId(blockId);
	}
}
