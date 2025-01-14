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

	public BlockDataAndPosition() {

	}

	public BlockDataAndPosition(BlockState state, BlockPos pos) {
		this.blockId = Block.getRawIdFromState(state);
		this.blockPosition = new BlockPosition(pos);
	}

	public BlockState toBlockState() {
		return Block.getStateFromRawId(blockId);
	}
}
