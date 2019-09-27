package Commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.*;

public class ctLoadTrader implements CommandExecutor {

    private FileConfiguration configuration;

    public void setFileConfiguration(FileConfiguration configuration){
        this.configuration = configuration;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // make sure the command was issued by a player
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Only a player can run this command");
            return false;
        }

        Player player = (Player) commandSender;

        // load all trader recipe configuration
        HashMap<String, List<MerchantRecipe>> recipeCollection = processRecipes();

        // Set the trading recipes to respective traders.
        for(String traderName : recipeCollection.keySet()){
            WanderingTrader targetTrader = getTargetTrader(player, traderName);

            if(targetTrader == null){
                player.sendMessage("A wandering trader by the name " + traderName + " was not found.");
                continue;
            }

            player.sendMessage("Loading custom trades for " + traderName);
            targetTrader.setRecipes(recipeCollection.get(traderName));

        }

        return true;
    }

    /**
     * Finds the wandering trader with the provided name. Search radius is 30x30x30 blocks
     * @param player Command sender
     * @param traderName Name of the wandering trader we are looking for
     * @return (1) Reference to the correct wandering trader with the custom name or (2) Null if not found
     */
    private WanderingTrader getTargetTrader(Player player, String traderName){
        Location playerLocation = player.getLocation();

        Collection<WanderingTrader> traders = playerLocation.getNearbyEntitiesByType(WanderingTrader.class,
                30, 30, 30);

        for (WanderingTrader trader: traders) {
            String customName = trader.getCustomName();
            if(customName == null) continue;

            if(customName.equalsIgnoreCase(traderName)){
                return trader;
            }
        }
        return null;
    }

    /**
     * Retrieves all trading recipes for all the traders from configuration and converts them to a bukkit usable form.
     * @return A hash map containing the trading recipe list for all defined traders.
     */
    private HashMap<String, List<MerchantRecipe>> processRecipes(){
        HashMap<String, List<MerchantRecipe>> recipeCollection = new HashMap<String, List<MerchantRecipe>>();

        String root = "traders";
        Set<String> sections =  configuration.getConfigurationSection(root).getKeys(false);

        /**
         * trader1: <-- Trader name
         *      1:  <-- An entry representing a merchant recipe
         *          sell:           <- This is what the merchant sells
         *              item: ~
         *              amount: ~
         *          buy:            <- This is what the merchants expects from the player
         *              buy1:
         *                  item: ~
         *                  amount: ~
         *              buy2: [optional] <- 2nd one is option. Don't add it if the merchant shouldn't expect more than 1 item from player
         *                  item: ~
         *                  amount: ~
         *
         */
        for (String traderName: sections) {
            List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();

            Set<String> yamlRecipes = configuration.getConfigurationSection(root + "." +traderName).getKeys(false);

            for(String yamlRecipe : yamlRecipes) {
                final String basePath = root + "." + traderName + "." + yamlRecipe;
                final String sellPath = basePath + ".sell";
                final String sellMaterialPath = sellPath + ".item";
                final String sellMaterialCountPath = sellPath + ".amount";

                final String buyPath = basePath + ".buy";

                String sellItemName = configuration.getString(sellMaterialPath);
                int sellItemAmount = configuration.getInt(sellMaterialCountPath);
                System.out.println("Processing sell item material: " + sellItemName);
                Material sellMaterial = Material.getMaterial(sellItemName);
                if(sellMaterial == null){
                    System.out.println(sellItemName + " Appears to be null... Skipping");
                    continue;
                }
                System.out.println("Selling " + Integer.toString(sellItemAmount) + " " + sellItemName);
                ItemStack sellItem = new ItemStack(sellMaterial, sellItemAmount);

                ItemStack[] buyItems = {null, null};

                // Load buy items for this configuration
                List<String> rawBuyItem = new ArrayList<String>(configuration.getConfigurationSection(buyPath).getKeys(false));
                for (int i = 0; i < rawBuyItem.size(); i++) {
                    String itemPath = buyPath + "." + rawBuyItem.get(i);
                    String itemNamePath = itemPath + "." + "item";
                    String itemAmountPath = itemPath + "." + "amount";

                    String buyItemName = configuration.getString(itemNamePath);
                    int buyItemAmount = configuration.getInt(itemAmountPath);
                    //System.out.println("Processing buy item material: " + buyItemName);

                    Material buyMaterial = Material.getMaterial(buyItemName);
                    if(buyMaterial == null){
                        System.out.println(buyItemName + " Appears to be null... Skipping");
                        continue;
                    }

                    buyItems[i] = new ItemStack(buyMaterial, buyItemAmount);
                }

                recipes.add(createMerchantRecipe(buyItems[0], buyItems[1], sellItem));
            }
            recipeCollection.put(traderName, recipes);
        }

        return recipeCollection;
    }

    /**
     * Creates a trading recipe using the provided items
     * @param item1 An item the trader is willing to take
     * @param item2 (optional, nullable) Another item the trader wants along with @item1
     * @param sellingItem The Item the trader wants to sell to the player
     * @return An instance of {@link MerchantRecipe} with max uses set to 100000 and experience gain is set to false.
     */
    private  MerchantRecipe createMerchantRecipe(ItemStack item1, ItemStack item2, ItemStack sellingItem){
        MerchantRecipe recipe = new MerchantRecipe(sellingItem, 100000); // no max-usage limit
        recipe.setExperienceReward(false); // player will not receive any experience from it
        recipe.addIngredient(item1);

        if(!isEmptyItem(item2)){
            recipe.addIngredient(item2);
        }
        return recipe;
    }

    private static boolean isEmptyItem(ItemStack item){
        return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
    }
}
