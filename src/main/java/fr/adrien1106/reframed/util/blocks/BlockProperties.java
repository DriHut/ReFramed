package fr.adrien1106.reframed.util.blocks;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;

public class BlockProperties {
    public static final BooleanProperty LIGHT = BooleanProperty.of("emits_light");
    public static final EnumProperty<Edge> EDGE = EnumProperty.of("edge", Edge.class);
    public static final IntProperty EDGE_FACE = IntProperty.of("face", 0, 1);
    public static final EnumProperty<Corner> CORNER = EnumProperty.of("corner", Corner.class);
    public static final IntProperty CORNER_FACE = IntProperty.of("face", 0, 2);
    public static final IntProperty CORNER_FEATURE = IntProperty.of("corner_feature", 0, 1);
    public static final EnumProperty<StairShape> STAIR_SHAPE = EnumProperty.of("shape", StairShape.class);
    public static final IntProperty HALF_LAYERS = IntProperty.of("layers", 1, 4);

}
