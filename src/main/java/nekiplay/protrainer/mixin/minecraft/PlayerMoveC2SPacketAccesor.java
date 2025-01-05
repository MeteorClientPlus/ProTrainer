package nekiplay.protrainer.mixin.minecraft;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMoveC2SPacketAccesor {
	@Mutable
	@Accessor("y")
	void setY(double y);

	@Accessor("y")
	double getY();

	@Mutable
	@Accessor("x")
	void setX(double x);

	@Accessor("x")
	double getX();

	@Mutable
	@Accessor("z")
	void setZ(double z);

	@Accessor("z")
	double getZ();

	@Mutable
	@Accessor("onGround")
	void setOnGround(boolean onGround);

	@Accessor("onGround")
	boolean getOnGround();
}
