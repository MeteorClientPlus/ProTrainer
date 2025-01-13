package nekiplay.protrainer.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockDataAndPosition {
	public int blockId = 0;
	public BlockPosition blockPosition;

	@Override
	public int hashCode() {
		return blockPosition.hashCode();
	}
}
