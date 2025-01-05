package nekiplay.protrainer.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockPosition {
	public double x;
	public double y;
	public double z;

	public BlockPosition(BlockPos pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	public BlockPosition(Vec3d pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	public BlockPosition() {

	}
}
