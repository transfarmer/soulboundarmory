package soulboundarmory.util;

import com.mojang.blaze3d.systems.RenderSystem;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.auoeke.reflect.Classes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.TriConsumer;
import soulboundarmory.SoulboundArmory;

public class Util {
    public static final boolean isPhysicalClient = FMLEnvironment.dist == Dist.CLIENT;

    private static final Map<Class<?>, IForgeRegistry<?>> registries = new Reference2ReferenceOpenHashMap<>();
    private static BiFunction<INameMappingService.Domain, String, String> mapper;

    public static <A, B> B nul(A... a) {
        return null;
    }

    public static <T> T cast(Object object) {
        return (T) object;
    }

    public static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static <T> T[] array(T... elements) {
        return elements;
    }

    public static <T> List<T> list(T... elements) {
        return ReferenceArrayList.wrap(elements);
    }

    public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
        var map = new Reference2ReferenceLinkedOpenHashMap<K, V>();
        Stream.of(entries).forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    public static <K, V, T extends Map<K, V>> T add(T map, Map.Entry<K, V> entry) {
        map.put(entry.getKey(), entry.getValue());
        return map;
    }

    public static boolean isClient() {
        return isPhysicalClient && (RenderSystem.isOnRenderThread() || Thread.currentThread().getName().equals("Game thread"));
    }

    public static MinecraftServer server() {
        return (MinecraftServer) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
    }

    public static boolean containsIgnoreCase(String string, String substring) {
        return Pattern.compile(Pattern.quote(substring), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(string).find();
    }

    public static boolean contains(Object target, Object... items) {
        return Arrays.asList(items).contains(target);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Set<T> hashSet(T... elements) {
        return new ObjectOpenHashSet<>(elements);
    }

    public static <T> Class<T> componentType(T... array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    public static <A> A[] add(A[] array, A element) {
        var union = Arrays.copyOf(array, array.length + 1);
        union[array.length] = element;

        return union;
    }

    public static <A> A[] add(A element, A[] array) {
        var union = (A[]) Array.newInstance(Util.componentType(array), array.length + 1);
        System.arraycopy(array, 0, union, 1, array.length);
        union[0] = element;

        return union;
    }

    public static String namespace() {
        var mod = ModLoadingContext.get().getActiveContainer();
        return mod instanceof MinecraftModContainer ? SoulboundArmory.ID : mod.getNamespace();
    }

    public static Identifier id(String path) {
        return new Identifier(namespace(), path);
    }

    public static void ifPresent(NbtCompound tag, String key, Consumer<NbtCompound> action) {
        var child = (NbtCompound) tag.get(key);

        if (child != null) {
            action.accept(child);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> newRegistry(String path, T... dummy) {
        return new RegistryBuilder<T>().setName(id(path)).setType(componentType(dummy)).create();
    }

    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> registry(Class<T> type) {
        if (type == null) {
            return null;
        }

        return (IForgeRegistry<T>) registries.computeIfAbsent(type, __ -> {
            var registry = RegistryManager.ACTIVE.getRegistry(type);
            return registry == null ? registry(cast(type.getSuperclass())) : registry;
        });
    }

    public static <K, V> void enumerate(Map<K, V> map, TriConsumer<K, V, Integer> action) {
        var count = 0;

        for (var entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), count++);
        }
    }

    public static <T> void enumerate(Iterable<T> iterable, ObjIntConsumer<T> action) {
        var count = 0;

        for (var element : iterable) {
            action.accept(element, count++);
        }
    }

    public static <T> void each(Iterable<? extends Reference<? extends T>> iterable, Consumer<? super T> action) {
        var iterator = iterable.iterator();

        while (iterator.hasNext()) {
            var reference = iterator.next();

            if (reference == null) {
                LogManager.getLogger("soulbound-armory").error("\uD83E\uDD28 Something's fishy.");
            } else if (!reference.refersTo(null)) {
                action.accept(reference.get());

                continue;
            }

            try {
                iterator.remove();
            } catch (IndexOutOfBoundsException __)  {}
        }
    }

    public static List<Type> arguments(Class<?> subtype, Class<?> supertype) {
        for (var type : Classes.supertypes(subtype)) {
            if (type == supertype) {
                for (var genericType : Classes.genericSupertypes(subtype)) {
                    if (genericType.equals(supertype)) {
                        return Arrays.asList(((ParameterizedType) genericType).getActualTypeArguments());
                    }
                }
            }
        }

        return null;
    }

    public static String mapClass(String name) {
        return mapper().apply(INameMappingService.Domain.CLASS, name);
    }

    public static String mapMethod(int number) {
        return mapper().apply(INameMappingService.Domain.METHOD, "m_%d_".formatted(number));
    }

    public static String mapField(int number) {
        return mapper().apply(INameMappingService.Domain.FIELD, "f_%d_".formatted(number));
    }

    private static BiFunction<INameMappingService.Domain, String, String> mapper() {
        return mapper == null ? mapper = Launcher.INSTANCE.environment().findNameMapping("srg").get() : mapper;
    }
}
