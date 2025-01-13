package nekiplay.protrainer.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
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

	@Override
	public int hashCode() {
		return new Vec3d(x, y, z).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockPosition other = (BlockPosition) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return z == other.z;
	}
}
