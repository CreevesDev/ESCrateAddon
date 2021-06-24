package me.cooldcb.escrateaddon;

import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.CrateAction;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.event.CrateInteractEvent;
import com.hazebyte.crate.api.util.PlayerUtil;
import me.thundertnt33.animatronics.Animatronic;
import net.minecraft.core.BlockPosition;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CrateEvents implements Listener {
    private final ESCrateAddon plugin;
    private boolean canUseVote = true;
    private boolean canUseAncient = true;
    private boolean canUseDivine = true;

    public CrateEvents(ESCrateAddon instance) {
        plugin = instance;
    }

    @EventHandler
    public void onCrateInteract(CrateInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getInventory().getItemInMainHand();

        if (!CrateAPI.getCrateRegistrar().isCrate(hand)) return;

        if (event.isCancelled()) return;

        final CrateAction playerAction = event.getAction();
        if (playerAction != CrateAction.OPEN) return;
        event.setCancelled(true);
        switch (event.getCrate().getCrateName()) {
            case "Vote": {
                doVoteCrateAnimation(event); break;
            }

            case "Ancient": {
                doAncientCrateAnimation(event); break;
            }

            case "Divine": {
                doDivineCrateAnimation(event); break;
            }
            default:
                event.setCancelled(false);
        }
    }

    public void doVoteCrateAnimation(CrateInteractEvent event) {
        Location crateLocation = event.getLocation();
        Player player = event.getPlayer();
        if (!crateLocation.equals(new Location(Bukkit.getWorld("quest-world"), -342, 90, -2176))) {
            player.sendMessage("§8§l[§d§lE§7§lS§8§l]§c You may not use " + event.getCrate().getCrateName() + " here.");
            return;
        }

        if (!canUseVote) return;
        canUseVote = false;
        PlayerUtil.removeAnItemInHand(player);

        playChestAnimation(crateLocation, true);

        new BukkitRunnable() {
            public void run() {
                playChestAnimation(crateLocation, false);
            }
        }.runTaskLater(plugin, 35);

        Animatronic animaVote = new Animatronic("Vote");
        animaVote.start();

        Crate crate = event.getCrate();
        List<Reward> prizes = crate.generatePrizes(player);

        EntityEquipment animaArmour = animaVote.getArmorstand().getEquipment();
        if (animaArmour != null) {
            animaArmour.setHelmet(prizes.get(0).getDisplayItem());
        }

        new BukkitRunnable() {
            public void run() {
                if (animaArmour != null) {
                    animaArmour.setHelmet(prizes.get(0).getDisplayItem());
                }
                crate.onRewards(player, prizes);
                new BukkitRunnable() {
                    public void run() {
                        animaVote.gotoStart();
                        canUseVote = true;
                        if (animaArmour != null) {
                            animaArmour.setHelmet(new ItemStack(Material.AIR));
                        }
                    }}.runTaskLater(plugin, 23);
            }}.runTaskLater(plugin, 21);
        return;
    }

    public void doAncientCrateAnimation(CrateInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getLocation().equals(new Location(Bukkit.getWorld("quest-world"), -403, 109, -2010))) {
            player.sendMessage("§8§l[§d§lE§7§lS§8§l]§c You may not use " + event.getCrate().getCrateName() + " here.");
            return;
        }

        if (!canUseAncient) return;
        canUseAncient = false;
        PlayerUtil.removeAnItemInHand(player);

        Location crateLocation = event.getLocation();

        playChestAnimation(crateLocation, true);

        new BukkitRunnable() {
            public void run() {
                playChestAnimation(crateLocation, false);
            }
        }.runTaskLater(plugin, 170);

        new BukkitRunnable() {
            double alpha = 0;
            public void run() {
                if (canUseAncient) return;

                alpha += Math.PI / 8;

                Location loc = crateLocation.clone().add(0.5, 7.5, 0.5);;
                Location firstLocation = loc.clone().add( (Math.cos( alpha )/2), 0, (Math.sin( alpha )/2) );
                Location secondLocation = loc.clone().add( (Math.cos( alpha + Math.PI )/2), 0, (Math.sin( alpha + Math.PI )/2) );
                loc.getWorld().spawnParticle( Particle.SPELL_WITCH, firstLocation, 0, 0, 0, 0, 0 );
                loc.getWorld().spawnParticle( Particle.SPELL_WITCH, secondLocation, 0, 0, 0, 0, 0 );
            }
        }.runTaskTimer(plugin, 0, 1);

        new BukkitRunnable() {
            double alpha = 0;
            public void run() {
                if (canUseAncient) return;

                alpha += Math.PI / 8;

                Location loc = crateLocation.clone().add(0.5, 1.5, -9.5);
                Location firstLocation = loc.clone().add( (Math.cos( alpha )/2), (Math.sin( alpha )/2), 0 );
                Location secondLocation = loc.clone().add( (Math.cos( alpha + Math.PI )/2), (Math.sin( alpha + Math.PI )/2), 0 );
                loc.getWorld().spawnParticle( Particle.SPELL_WITCH, firstLocation, 0, 0, 0, 0, 0 );
                loc.getWorld().spawnParticle( Particle.SPELL_WITCH, secondLocation, 0, 0, 0, 0, 0 );
            }
        }.runTaskTimer(plugin, 0, 1);


        Animatronic animaAncient = new Animatronic("Ancient");
        animaAncient.start();

        crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 0.1f);

        new BukkitRunnable() {
            public void run() {
                crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_PORTAL_TRIGGER, 1f, 2f);

                new BukkitRunnable() {
                    public void run() {
                        Crate crate = event.getCrate();
                        List<Reward> prizes = crate.generatePrizes(player);

                        EntityEquipment animaArmour = animaAncient.getArmorstand().getEquipment();
                        if (animaArmour != null) {
                            animaArmour.setHelmet(prizes.get(0).getDisplayItem());
                        }

                        crateLocation.getWorld().spawnParticle(Particle.ITEM_CRACK, crateLocation.clone().add(0.5, 1.5, 0.5), 8, 0, 0, 0, 0.1, new ItemStack(Material.ENDER_EYE));
                        crateLocation.getWorld().spawnParticle(Particle.SPIT, crateLocation.clone().add(0.5, 1.5, 0.5), 5, 0, 0, 0, 0);
                        crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.6f);

                        new BukkitRunnable() {
                            public void run() {
                                crateLocation.getWorld().spawnParticle(Particle.SPIT, crateLocation.clone().add(0.5, 1.5, 0.5), 5, 0, 0, 0, 0);
                                crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                crate.onRewards(player, prizes);

                                new BukkitRunnable() {
                                    public void run() {
                                        animaAncient.gotoStart();
                                        canUseAncient = true;
                                    }}.runTaskLater(plugin, 30);
                            }}.runTaskLater(plugin, 54);
                    }}.runTaskLater(plugin, 64);
            }}.runTaskLater(plugin, 126);


        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < 20; i++) {
                    int particleCount = (i * i) / 15;
                    Location particleLocation = crateLocation.clone().add(0.5, 1.25, 0.5);
                    new BukkitRunnable() {
                        public void run() {
                            particleLocation.getWorld().spawnParticle(Particle.PORTAL, particleLocation, particleCount, 0, 0, 0, 2);
                        }
                    }.runTaskLater(plugin, (i * 5));
                }
            }
        }.runTaskLater(plugin, 20);
    }

    public void doDivineCrateAnimation(CrateInteractEvent event) {
        Player player = event.getPlayer();
        Crate crate = event.getCrate();
        String crateName = crate.getCrateName();
        if (!event.getLocation().equals(new Location(Bukkit.getWorld("quest-world"), -259, 127, -2079))) {
            player.sendMessage("§8§l[§d§lE§7§lS§8§l]§c You may not use " + crateName + " here.");
            return;
        }

        if (!canUseDivine) return;
        canUseDivine = false;
        PlayerUtil.removeAnItemInHand(player);

        Animatronic animaDivine = new Animatronic("Divine");
        animaDivine.start();

        Location particleLocation = event.getLocation().clone().add(0.5, 1.5, 0.5);

        String[][] particleScheduler = {
                {"SPIT", "3", "30"},
                {"SPIT", "3", "40"},
                {"SPIT", "3", "50"},
                {"LAVA", "15", "70"},
                {"SPIT", "5", "70"},
                {"SPIT", "5", "173"}
        };
        particleSchedule(particleScheduler, particleLocation);

        String[][] soundScheduler = {
                {"BLOCK_SCAFFOLDING_HIT", "1", "0.5", "30"},
                {"BLOCK_SCAFFOLDING_HIT", "1", "0.5", "40"},
                {"BLOCK_SCAFFOLDING_HIT", "1", "0.5", "50"},
                {"ENTITY_GENERIC_EXPLODE", "0.7", "1", "70"},
                {"BLOCK_NOTE_BLOCK_PLING", "1", "2", "173"}
        };
        soundSchedule(soundScheduler, particleLocation);

        new BukkitRunnable() {
            public void run() {
                List<Reward> prizes = crate.generatePrizes(player);

                EntityEquipment animaArmour = animaDivine.getArmorstand().getEquipment();
                if (animaArmour != null) {
                    animaArmour.setHelmet(prizes.get(0).getDisplayItem());
                }

                new BukkitRunnable() {
                    public void run() {
                        EntityEquipment animaArmour = animaDivine.getArmorstand().getEquipment();
                        if (animaArmour != null) {
                            animaArmour.setHelmet(prizes.get(0).getDisplayItem());
                        }

                        new BukkitRunnable() {
                            public void run() {
                                crate.onRewards(player, prizes);

                                new BukkitRunnable() {
                                    public void run() {
                                        animaDivine.gotoStart();
                                        canUseDivine = true;
                                    }}.runTaskLater(plugin, 20);
                            }}.runTaskLater(plugin, 61);
                    }}.runTaskLater(plugin, 42);
            }}.runTaskLater(plugin, 70);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        if (armorStand.getCustomName() == null) return;
        if (armorStand.getCustomName().equals("Vote") || armorStand.getCustomName().equals("Ancient") || armorStand.getCustomName().equals("Divine")) event.setCancelled(true);
    }

    public void particleSchedule(String[][] particleScheduleArr, Location location) {
        for (String[] strings : particleScheduleArr) {
            Particle particleType = Particle.valueOf(strings[0]);
            int particleCount = Integer.parseInt(strings[1]);
            int delay = Integer.parseInt(strings[2]);

            new BukkitRunnable() {
                public void run() {
                    location.getWorld().spawnParticle(particleType, location, particleCount, 0, 0, 0, 0);
                }
            }.runTaskLater(plugin, delay);
        }
    }

    public void soundSchedule(String[][] soundScheduleArr, Location location) {
        for (String[] strings : soundScheduleArr) {
            Sound sound = Sound.valueOf(strings[0]);
            float volume = Float.parseFloat(strings[1]);
            float pitch = Float.parseFloat(strings[2]);
            int delay = Integer.parseInt(strings[3]);

            new BukkitRunnable() {
                public void run() {
                    location.getWorld().playSound(location, sound, volume, pitch);
                }
            }.runTaskLater(plugin, delay);
        }
    }

    public void playChestAnimation(Location chestLoc, boolean setOpen) {
        BlockPosition chestPos = new BlockPosition(chestLoc.getX(), chestLoc.getY(), chestLoc.getZ());
        Material chestType = chestLoc.getBlock().getType();
        int playerCount = 0;
        if (setOpen) playerCount = 1;
        ((CraftWorld) chestLoc.getWorld()).getHandle().playBlockAction(chestPos, CraftMagicNumbers.getBlock(chestType), 1, playerCount);
    }
}
