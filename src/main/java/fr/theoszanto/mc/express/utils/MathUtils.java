package fr.theoszanto.mc.express.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MathUtils {
	private MathUtils() {
		throw new UnsupportedOperationException();
	}

	public static int minMax(int min, int value, int max) {
		return Math.max(min, Math.min(value, max));
	}

	public static double minMax(double min, double value, double max) {
		return Math.max(min, Math.min(value, max));
	}

	public static double round(double value, int precision) {
		double pow = Math.pow(10, precision);
		return Math.round(value * pow) / pow;
	}

	public static int nextAvailableInt(@NotNull Collection<@NotNull String> values) {
		int max = -1;
		for (String value : values) {
			try {
				int i = Integer.parseInt(value);
				if (i > max)
					max = i;
			} catch (NumberFormatException ignored) {}
		}
		return max + 1;
	}

	public static int random(int min, int max) {
		return min + (int) Math.floor(Math.random() * (max - min));
	}

	public static long random(long min, long max) {
		return min + (long) Math.floor(Math.random() * (max - min));
	}

	public static float random(float min, float max) {
		return min + (float) Math.random() * (max - min);
	}

	public static double random(double min, double max) {
		return min + Math.random() * (max - min);
	}

	public static @NotNull Duration random(@NotNull Duration min, @NotNull Duration max) {
		return Duration.ofMillis(random(min.toMillis(), max.toMillis()));
	}

	public static @NotNull Location random(@NotNull Location min, @NotNull Location max) {
		if (!Objects.equals(min.getWorld(), max.getWorld()))
			throw new IllegalArgumentException("Could not find random location between two distinct worlds");
		return new Location(min.getWorld(),
				random(min.getX(), max.getX()),
				random(min.getY(), max.getY()),
				random(min.getZ(), max.getZ()),
				random(min.getYaw(), max.getYaw()),
				random(min.getPitch(), max.getPitch()));
	}

	public static @NotNull Color randomColor() {
		return Color.fromRGB(random(0, 0xFF_FF_FF + 1));
	}

	public static <T> @NotNull T random(@NotNull T @NotNull[] elements) throws IllegalArgumentException {
		if (elements.length == 0)
			throw new IllegalArgumentException("Could not find random element in empty array");
		return elements[random(0, elements.length)];
	}

	public static <T> @NotNull T random(@NotNull List<@NotNull T> elements) throws IllegalArgumentException {
		if (elements.isEmpty())
			throw new IllegalArgumentException("Could not find random element in empty list");
		return elements.get(random(0, elements.size()));
	}

	public static double totalWeight(@NotNull Collection<? extends Weighted> elements) {
		return elements.stream().mapToDouble(Weighted::getWeight).sum();
	}

	public static <T extends Weighted> @NotNull T weightedRandom(@NotNull Collection<@NotNull T> elements) throws IllegalArgumentException {
		double random = random(0, totalWeight(elements));
		double weight = 0;
		for (T reward : elements) {
			weight += reward.getWeight();
			if (random < weight)
				return reward;
		}
		throw new IllegalArgumentException("Could not find random element in collection. Is it empty?");
	}

	public static int @NotNull[] numbers(int min, int max) {
		int[] numbers = new int[max - min];
		for (int i = 0, n = min; i < numbers.length; i++, n++) {
			numbers[i] = n;
		}
		return numbers;
	}
}
