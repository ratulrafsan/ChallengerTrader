import Commands.ctLoadTrader;
import org.bukkit.plugin.java.JavaPlugin;


public class PluginEntry extends JavaPlugin{
    @Override
    public void onEnable(){

        saveDefaultConfig();
        // Pass and instance of the configuration file to the command executor
        ctLoadTrader setTraderCommandExecutor = new ctLoadTrader();
        setTraderCommandExecutor.setFileConfiguration(getConfig());

        // Setup commands
        this.getCommand("ctLoadTrader").setExecutor(setTraderCommandExecutor);
    }

    @Override
    public void onDisable(){
        //Save config, clear cache etc.
    }

}