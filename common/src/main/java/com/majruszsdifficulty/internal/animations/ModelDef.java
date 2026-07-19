package com.majruszsdifficulty.internal.animations;

import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

public class ModelDef {
    private static final Codec<Vector2i> VECTOR2I_CODEC = Codec.INT.listOf().comapFlatMap(
            values -> Util.fixedSize(values, 2).map(Animation::to2d),
            Animation::to2d
    );
    private static final Codec<CubeDef> CUBE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin", new Vector3f()).forGetter(value -> value.origin),
            ExtraCodecs.VECTOR3F.optionalFieldOf("size", new Vector3f()).forGetter(value -> value.size),
            Codec.FLOAT.optionalFieldOf("inflate", 0.0f).forGetter(value -> value.inflate),
            VECTOR2I_CODEC.optionalFieldOf("uv", new Vector2i()).forGetter(value -> value.uv)
    ).apply(instance, CubeDef::new));
    private static final Codec<BoneDef> BONE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "body").forGetter(value -> value.name),
            Codec.STRING.optionalFieldOf("parent").forGetter(value -> Optional.ofNullable(value.parent)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("pivot", new Vector3f()).forGetter(value -> value.globalPivot),
            ExtraCodecs.VECTOR3F.optionalFieldOf("rotation").forGetter(value -> Optional.ofNullable(value.rotation)),
            CUBE_CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(value -> value.cubes)
    ).apply(instance, BoneDef::new));
    private static final Codec<DescriptionDef> DESCRIPTION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("texture_width", 32).forGetter(value -> value.width),
            Codec.INT.optionalFieldOf("texture_height", 32).forGetter(value -> value.height)
    ).apply(instance, DescriptionDef::new));
    private static final Codec<GeometryDef> GEOMETRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DESCRIPTION_CODEC.fieldOf("description").forGetter(value -> value.description),
            BONE_CODEC.listOf().optionalFieldOf("bones", List.of()).forGetter(value -> value.bones)
    ).apply(instance, GeometryDef::new));
    public static final Codec<ModelDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GEOMETRY_CODEC.listOf().fieldOf("minecraft:geometry").forGetter(value -> value.geometries)
    ).apply(instance, ModelDef::new));

    public List<GeometryDef> geometries = new ArrayList<>();

    public ModelDef() {
    }

    private ModelDef(List<GeometryDef> geometries) {
        this.geometries = new ArrayList<>(geometries);
    }

    @OnlyIn(Dist.CLIENT)
    public LayerDefinition toLayerDefinition() {
        GeometryDef geometry = this.geometries.get(0);
        MeshDefinition mesh = new MeshDefinition();
        Map<String, Vector3f> offsets = new HashMap<>();
        Map<String, PartDefinition> parts = new HashMap<>();
        for (BoneDef bone : geometry.bones) {
            CubeListBuilder cubes = CubeListBuilder.create();
            for (CubeDef cube : bone.cubes) {
                Vector3f position = AnyPos.from(cube.origin).sub(bone.globalPivot).add(0.0f, cube.size.y, 0.0f).mul(1.0f, -1.0f, 1.0f).vec3f();
                cubes.texOffs(cube.uv.x, cube.uv.y)
                        .addBox(position.x, position.y, position.z, cube.size.x, cube.size.y, cube.size.z, new CubeDeformation(cube.inflate));
            }

            if (bone.parent != null) {
                bone.pivot = AnyPos.from(bone.globalPivot).sub(offsets.get(bone.parent)).mul(1.0f, -1.0f, 1.0f).vec3f();
            } else {
                bone.pivot = AnyPos.from(bone.globalPivot).mul(1.0f, -1.0f, 1.0f).add(0.0, 24.0, 0.0).vec3f();
            }

            PartPose pose;
            if (bone.rotation != null) {
                float toRadiansScale = (float) Math.PI / 180.0f;
                pose = PartPose.offsetAndRotation(bone.pivot.x, bone.pivot.y, bone.pivot.z, bone.rotation.x * toRadiansScale, bone.rotation.y * toRadiansScale, bone.rotation.z * toRadiansScale);
            } else {
                pose = PartPose.offset(bone.pivot.x, bone.pivot.y, bone.pivot.z);
            }

            offsets.put(bone.name, bone.globalPivot);
            parts.put(bone.name, (bone.parent != null ? parts.get(bone.parent) : mesh.getRoot()).addOrReplaceChild(bone.name, cubes, pose));
        }

        return LayerDefinition.create(mesh, geometry.description.width, geometry.description.height);
    }

    public static class GeometryDef {
        public DescriptionDef description = new DescriptionDef();
        public List<BoneDef> bones = new ArrayList<>();

        public GeometryDef() {
        }

        private GeometryDef(DescriptionDef description, List<BoneDef> bones) {
            this.description = description;
            this.bones = new ArrayList<>(bones);
        }
    }

    public static class DescriptionDef {
        public int width = 32;
        public int height = 32;

        public DescriptionDef() {
        }

        private DescriptionDef(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static class BoneDef {
        public String name = "body";
        public String parent = null;
        public Vector3f globalPivot = new Vector3f(0.0f, 0.0f, 0.0f);
        public Vector3f pivot = new Vector3f(0.0f, 0.0f, 0.0f);
        public Vector3f rotation = null;
        public List<CubeDef> cubes = new ArrayList<>();

        public BoneDef() {
        }

        private BoneDef(String name, Optional<String> parent, Vector3f pivot, Optional<Vector3f> rotation, List<CubeDef> cubes) {
            this.name = name;
            this.parent = parent.orElse(null);
            this.globalPivot = new Vector3f(pivot);
            this.rotation = rotation.map(Vector3f::new).orElse(null);
            this.cubes = new ArrayList<>(cubes);
        }
    }

    public static class CubeDef {
        public Vector3f origin = new Vector3f(0.0f, 0.0f, 0.0f);
        public Vector3f size = new Vector3f(0.0f, 0.0f, 0.0f);
        public Float inflate = 0.0f;
        public Vector2i uv = new Vector2i(0, 0);

        public CubeDef() {
        }

        private CubeDef(Vector3f origin, Vector3f size, Float inflate, Vector2i uv) {
            this.origin = new Vector3f(origin);
            this.size = new Vector3f(size);
            this.inflate = inflate;
            this.uv = new Vector2i(uv);
        }
    }
}
