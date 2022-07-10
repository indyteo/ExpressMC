package fr.theoszanto.mc.express.utils;

import fr.theoszanto.mc.express.ExpressObject;
import fr.theoszanto.mc.express.ExpressPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Registry<P extends ExpressPlugin<P>, K extends Comparable<@NotNull K>, V> extends ExpressObject<P> implements Iterable<@NotNull V> {
	private final @NotNull SortedMap<@NotNull K, @NotNull V> values = new TreeMap<>();
	@UnmodifiableView
	private final @NotNull Set<@NotNull K> unmodifiableKeys = Collections.unmodifiableSet(this.values.keySet());
	@UnmodifiableView
	private final @NotNull Collection<@NotNull V> unmodifiableValues = Collections.unmodifiableCollection(this.values.values());
	private final @NotNull String name;

	public Registry(@NotNull P plugin, @NotNull String name) {
		super(plugin);
		this.name = name;
	}

	public void reset() {
		this.values.clear();
	}

	@UnmodifiableView
	public @NotNull Set<@NotNull K> keys() {
		return this.unmodifiableKeys;
	}

	@UnmodifiableView
	public @NotNull Collection<@NotNull V> list() {
		return this.unmodifiableValues;
	}

	@Override
	public @NotNull Iterator<@NotNull V> iterator() {
		return this.list().iterator();
	}

	protected final void set(@NotNull K key, @NotNull V value) {
		V old = this.values.put(key, value);
		if (old != null)
			this.warn("Overwriting old value in registry \"" + this.name + "\" at key \"" + key + "\".");
	}

	public void register(@NotNull K key, @NotNull V value) throws IllegalArgumentException {
		if (this.values.containsKey(key))
			throw new IllegalArgumentException("A value already has been register for this key");
		this.set(key, value);
	}

	protected @Nullable V getRaw(@NotNull K key) {
		return this.values.get(key);
	}

	public @NotNull V get(@NotNull K key) throws IllegalArgumentException {
		V value = this.getRaw(key);
		if (value == null)
			throw new IllegalArgumentException("No value was registered for this key");
		return value;
	}

	public void delete(@NotNull K key) throws IllegalArgumentException {
		V old = this.values.remove(key);
		if (old == null)
			throw new IllegalArgumentException("No value was registered for this key");
	}
}
