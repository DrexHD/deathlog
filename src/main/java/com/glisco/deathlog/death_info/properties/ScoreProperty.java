package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

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
    public Text formatted() {
        return Text.translatable(
                "deathlog.deathinfoproperty.score.value",
                score, levels, xp
        );
    }

    @Override
    public void writeNbt(WriteView view) {
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
    public void restore(ServerPlayerEntity player) {
        player.experienceProgress = progress;
        player.setExperienceLevel(levels);
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
        public ScoreProperty readFromNbt(ReadView view) {

            int score = view.getInt("Score", 0);
            int levels = view.getInt("Levels", 0);
            float progress = view.getFloat("Progress", 0);
            int xp = view.getInt("XP", 0);

            return new ScoreProperty(score, levels, progress, xp);
        }
    }
}
