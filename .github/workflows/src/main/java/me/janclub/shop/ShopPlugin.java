package me.janclub.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopPlugin extends JavaPlugin implements Listener {
    private Economy economy;
    private final Map<Integer, ShopItem> items = new HashMap<>();

    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> provider =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            getLogger().severe("Vault ekonomi bulunamadi!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        economy = provider.getProvider();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("shop").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player player) openShop(player);
            return true;
        });
    }

    private void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5JANCLUB §8| §dMağaza");
        inv.setItem(4, named(Material.NETHER_STAR, "§d§lJANCLUB SHOP", "§7Satın almak için eşyaya tıkla"));
        add(inv, 10, Material.ENDER_CHEST, "Ender Sandığı", 2500);
        add(inv, 11, Material.ENDER_PEARL, "Ender İncisi", 75);
        add(inv, 12, Material.END_STONE, "End Taşı", 8);
        add(inv, 13, Material.DRAGON_BREATH, "Ejderha Nefesi", 1000);
        add(inv, 14, Material.END_ROD, "End Çubuğu", 100);
        add(inv, 15, Material.CHORUS_FRUIT, "Chorus Meyvesi", 10);
        add(inv, 16, Material.POPPED_CHORUS_FRUIT, "Patlamış Chorus", 15);
        add(inv, 19, Material.SHULKER_SHELL, "Shulker Kabuğu", 350);
        add(inv, 20, Material.SHULKER_BOX, "Shulker Kutusu", 800);
        add(inv, 21, Material.BLAZE_ROD, "Blaze Çubuğu", 150);
        add(inv, 22, Material.NETHER_WART, "Nether Wart", 15);
        add(inv, 23, Material.GLOWSTONE_DUST, "Glowstone Tozu", 15);
        add(inv, 24, Material.MAGMA_CREAM, "Magma Kremi", 15);
        add(inv, 25, Material.GHAST_TEAR, "Ghast Gözyaşı", 350);
        add(inv, 28, Material.QUARTZ, "Kuvars", 15);
        add(inv, 29, Material.SOUL_SAND, "Ruh Kumu", 50);
        add(inv, 30, Material.MAGMA_BLOCK, "Magma Bloğu", 35);
        add(inv, 31, Material.CRYING_OBSIDIAN, "Ağlayan Obsidyen", 150);
        add(inv, 32, Material.OBSIDIAN, "Obsidyen", 100);
        add(inv, 33, Material.GLOWSTONE, "Glowstone", 100);
        add(inv, 34, Material.TOTEM_OF_UNDYING, "Ölümsüzlük Totemi", 1250);
        add(inv, 37, Material.GOLDEN_APPLE, "Altın Elma", 250);
        add(inv, 38, Material.EXPERIENCE_BOTTLE, "XP Şişesi", 100);
        add(inv, 39, Material.ARROW, "Ok", 250);
        add(inv, 40, Material.POTATO, "Patates", 75);
        add(inv, 41, Material.SWEET_BERRIES, "Tatlı Meyveler", 50);
        add(inv, 42, Material.MELON_SLICE, "Karpuz Dilimi", 10);
        add(inv, 43, Material.CARROT, "Havuç", 65);
        add(inv, 44, Material.APPLE, "Elma", 25);
        player.openInventory(inv);
    }

    private void add(Inventory inv, int slot, Material mat, String name, double price) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d" + name);
        meta.setLore(Arrays.asList("§7Fiyat: §a" + price + "$", "§eSatın almak için tıkla"));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        items.put(slot, new ShopItem(mat, name, price));
    }

    private ItemStack named(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("§5JANCLUB §8| §dMağaza")) return;
        event.setCancelled(true);
        ShopItem item = items.get(event.getRawSlot());
        if (item == null) return;
        if (economy.getBalance(player) < item.price) {
            player.sendMessage("§cParan yetersiz!");
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cEnvanterin dolu!");
            return;
        }
        economy.withdrawPlayer(player, item.price);
        player.getInventory().addItem(new ItemStack(item.material));
        player.sendMessage("§a" + item.name + " satın aldın! §7-" + item.price + "$");
    }

    private record ShopItem(Material material, String name, double price) {}
              }
