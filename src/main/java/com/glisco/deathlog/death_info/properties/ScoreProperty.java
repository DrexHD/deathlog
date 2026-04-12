package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;

public class ScoreProperty implements RestorableDeathInfoProperty {

    private final int score;
    private final int levels;
    private final float progress;

    private final int xp;

    public ScoreProperty(int score, int level, float progress, int xp) {
        this.score = score;
        this.levels = level;
        this.progress = progress;
        this.xp = xp;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Component formatted() {
        return Component.translatable(
                "deathlog.deathinfoproperty.score.value",
                score, levels, xp
        );
    }

    @Override
    public void writeNbt(ValueOutput view) {
        view.putInt("Score", score);
        view.putInt("Levels", levels);
        view.putFloat("Progress", progress);
        view.putInt("XP", xp);
    }

    @Override
    public String toSearchableString() {
        return xp + " " + levels;
    }

    @Override
    public void restore(ServerPlayer player) {
        player.experienceProgress = progress;
        player.setExperienceLevels(levels);
    }

    public static class Type extends DeathInfoPropertyType<ScoreProperty> {

        public static final Type INSTANCE = new Type();

        private Type() {
            super("deathlog.deathinfoproperty.score", "score");
        }

        @Override
        public boolean displayedInInfoView() {
            return true;
        }

        @Override
        public ScoreProperty readFromNbt(ValueInput view) {

            int score = view.getIntOr("Score", 0);
            int levels = view.getIntOr("Levels", 0);
            float progress = view.getFloatOr("Progress", 0);
            int xp = view.getIntOr("XP", 0);

            return new ScoreProperty(score, levels, progress, xp);
        }
    }
}
