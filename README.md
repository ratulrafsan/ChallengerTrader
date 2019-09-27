# ChallengerTrader
A custom wandering trader's trade management plugin written for Challenger craft minecraft server.

# Configuration

The configuration is setup in the following format:

```$xslt
traders: <-- This the root node. It must exist and must be "traders:"
    trader name: <-- You can add multiple traders under the root node.
        1: <-- Can be anything, honestly. Its the name of one trade recipe.
            sell: <-- This is what the trader will give to player
                item: DIAMOND_HOE <-- Item name
                amount: 1 <-- How many? Make sure you follow the standard minecraft stacking rule.
            buy: <-- This is what the trader wants from the player. Trader may request upto two different items
                buy1: <-- First item the trader wants
                    item: GRASS_BLOCK 
                    amount: 50
                buy2: <-- This is optional. You don't have to add this if you want the trader to be happy with only one item.
                   item: BONE
                   amount: 20
        2: <-- Another example with only one trader "wants".
            sell:
                item: BAMBOO
                amount: 32
            buy:
                buy1:
                    item: SALMON_SPAWN_EGG
                    amount: 20 
```

# Usage
First, edit the plugin's config.yml with your desired trading recipes. Then spawn a wandering trader and give him a custom name.
Make sure his name matches the trader name you set in the config. Then, do ``` \ctl ```or ``` \ctLoadTrader ``` to load that trader's custom trade information for the configuration file.
**Make sure the trader is within 30 block radius around you. Otherwise the plugin won't register that trader.** 

If you make changes to the configuration file, you can load the updated file using ``` \ctr ``` or ``` \ctreload ``` command.
You must perform ```\ctl``` again for the changes to take effect. The block radius limit still applies here.

# Permissions
All commands are exclusive to server operators by default. But you may change them by updating permission.yml of your server.
```ct.reload``` refers to the ```\ctr``` or ```\ctreload``` command.
```ct.load``` refers to the ```\ctl``` or ```\ctLoadTrader``` command. 
