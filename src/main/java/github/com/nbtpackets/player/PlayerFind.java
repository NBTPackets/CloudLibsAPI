package github.com.nbtpackets.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import github.com.nbtpackets.util.scheduler.Task;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class PlayerFind {

    private final List<Player> players;

    private PlayerFind(Collection<? extends Player> players) {
        this.players = List.copyOf(players);
    }

    public static PlayerFind all() {
        return new PlayerFind(Bukkit.getOnlinePlayers());
    }

    public static Optional<Player> name(String name) {
        return Optional.ofNullable(Bukkit.getPlayerExact(name));
    }

    public static Optional<Player> uuid(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public static boolean isOnline(String name) {
        return Bukkit.getPlayerExact(name) != null;
    }

    public PlayerFind matching(Predicate<Player> condition) {
        if (condition == null) return this;

        return new PlayerFind(
                players.stream()
                        .filter(condition)
                        .toList()
        );
    }

    public PlayerFind inWorld(String worldName) {
        return Optional.ofNullable(worldName)
                .map(name -> matching(p -> p.getWorld().getName().equals(name)))
                .orElse(this);
    }

    public PlayerFind inWorld(World world) {
        return Optional.ofNullable(world)
                .map(w -> matching(p -> p.getWorld().equals(w)))
                .orElse(this);
    }

    public PlayerFind withPermission(String permission) {
        return Optional.ofNullable(permission)
                .map(perm -> matching(p -> p.hasPermission(perm)))
                .orElse(this);
    }

    public PlayerFind withoutPermission(String permission) {
        return Optional.ofNullable(permission)
                .map(perm -> matching(p -> !p.hasPermission(perm)))
                .orElse(this);
    }

    public PlayerFind withGamemode(GameMode gamemode) {
        return Optional.ofNullable(gamemode)
                .map(mode -> matching(p -> p.getGameMode() == mode))
                .orElse(this);
    }

    public PlayerFind except(Player excluded) {
        return Optional.ofNullable(excluded)
                .map(ex -> matching(p -> !p.equals(ex)))
                .orElse(this);
    }

    public PlayerFind except(Predicate<Player> condition) {
        return Optional.ofNullable(condition)
                .map(cond -> matching(cond.negate()))
                .orElse(this);
    }

    public PlayerFind delayed(long delayTicks, Consumer<PlayerFind> chain) {
        Task.later(delayTicks, () -> {
            List<Player> snapshot = players.stream()
                    .filter(Player::isOnline)
                    .toList();

            chain.accept(new PlayerFind(snapshot));
        });

        return this;
    }

    public PlayerFind repeating(long delay, long period, Consumer<PlayerFind> chain) {
        Task.timer(delay, period, () -> {
            List<Player> snapshot = players.stream()
                    .filter(Player::isOnline)
                    .toList();

            if (!snapshot.isEmpty()) {
                chain.accept(new PlayerFind(snapshot));
            }
        });

        return this;
    }

    public Optional<Player> first() {
        return players.stream().findFirst();
    }

    public List<Player> asList() {
        return players;
    }

    public Set<Player> asSet() {
        return Set.copyOf(players);
    }

    public List<String> asNames() {
        return players.stream()
                .map(Player::getName)
                .toList();
    }

    public List<UUID> asUuids() {
        return players.stream()
                .map(Player::getUniqueId)
                .toList();
    }

    public List<Player> random(int count) {
        if (count <= 0 || players.isEmpty()) return List.of();

        List<Player> copy = new ArrayList<>(players);
        Collections.shuffle(copy, ThreadLocalRandom.current());

        return copy.stream()
                .limit(count)
                .toList();
    }

    public void forEach(Consumer<Player> action) {
        players.forEach(action);
    }

    public Stream<Player> stream() {
        return players.stream();
    }

    public int count() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }
}