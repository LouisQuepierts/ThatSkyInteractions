package net.quepierts.thatskyinteractions.feature.expression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerExpressionManager extends DataSyncManager<ExpressionSet> {

    private static final String FOLDER = "expression/definition";

    @Getter
    private static final PlayerExpressionManager instance = new PlayerExpressionManager();

    private Map<Identifier, ExpressionSet>  sets        = Map.of();
    private List<Identifier>                byOrdinal   = List.of();
    private Map<Identifier, Expression>     expressions = Map.of();

    PlayerExpressionManager() {
        super(
                ExpressionSet.CODEC,
                ExpressionSet.STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.registerBefore(instance, ThatSkyInteractions.location("animation/definition"));
    }

    @SubscribeEvent
    public static void onRegisterPlayerAnimation(final RegisterPlayerAnimationEvent event) {
        for (final var entry : instance.sets.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue();
            final var leveled = value.leveled();

            var level = leveled ? 1 : 0;
            for (final var expression : value.expressions()) {
                expression.onRegisterPlayerAnimation(event, key, level);
                level++;
            }
        }
    }

    public @Nullable Expression get(@NonNull Identifier identifier) {
        return this.expressions.get(identifier);
    }

    public @Nullable Expression get(@NonNull Identifier identifier, int level) {
        final var set = this.sets.get(identifier);
        return set != null ? set.expressions().get(level - 1) : null;
    }

    public @Nullable ExpressionSet getSet(@NonNull Identifier identifier) {
        return this.sets.get(identifier);
    }

    @Override
    protected void apply(@NonNull Map<Identifier, ExpressionSet> preparations) {
        final var builder0 = ImmutableMap.<Identifier, ExpressionSet>builder();
        final var builder1 = ImmutableMap.<Identifier, Expression>builder();
        final var builder2 = ImmutableList.<Identifier>builder();

        for (final var entry : preparations.entrySet()) {
            final var identifier = entry.getKey();
            final var set = entry.getValue();
            final var leveled = set.leveled();

            builder0.put(identifier, set);
            builder2.add(identifier);

            if (!leveled) {
                final var first = set.expressions().getFirst();
                first.onGenerateData(identifier, 0);
                builder1.put(identifier, first);
            } else {
                int level = 1;
                for (final var expression : set.expressions()) {
                    expression.onGenerateData(identifier, level);
                    final var id = identifier.withSuffix("_" + level);
                    builder1.put(id, expression);
                    level++;
                }
            }
        }

        NeoForge.EVENT_BUS.post(new RegisterExpressionEvent(builder1::put));

        this.sets = builder0.build();
        this.byOrdinal = builder2.build();
        this.expressions = builder1.build();

        log.info("Loaded {} expression sets", this.sets.size());
        log.info("Loaded {} expressions", this.expressions.size());
    }

    public List<Identifier> ordinal() {
        return this.byOrdinal;
    }

    public Collection<Expression> expressions() {
        return this.expressions.values();
    }

    public Iterable<Identifier> identifiers() {
        return this.expressions.keySet();
    }
}
