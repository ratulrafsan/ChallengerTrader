import Commands.ctLoadTrader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class PluginEntry extends JavaPlugin{

    private ctLoadTrader setTraderCommandExecutor;
    @Override
    public void onEnable(){

        saveDefaultConfig();
        // Pass and instance of the configuration file to the command executor
        setTraderCommandExecutor = new ctLoadTrader();
        setTraderCommandExecutor.setFileConfiguration(getConfig());

        // Setup commands
        this.getCommand("ctLoadTrader").setExecutor(setTraderCommandExecutor);
    }

    @Override
    public void onDisable(){
        //Save config, clear cache etc.
    }

    /*
        Plugin configuration reloader.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] params){
        if(command.getName().equalsIgnoreCase("ctreload")){
            File cFile = new File(getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);
            sender.sendMessage("Reloaded configuration for Challenger Trader plugin. " +
                    "Please perform /ctLoadTrader within 30 blocks of your traders for the updated to take effect.");

            setTraderCommandExecutor.setFileConfiguration(config);
        }
        return false;
    }

}