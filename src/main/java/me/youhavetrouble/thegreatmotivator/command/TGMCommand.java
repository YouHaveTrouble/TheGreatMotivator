package me.youhavetrouble.thegreatmotivator.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.youhavetrouble.moneypit.EconomyResponse;
import me.youhavetrouble.thegreatmotivator.TheGreatMotivator;
import org.bukkit.OfflinePlayer;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TGMCommand {

    public TGMCommand(TheGreatMotivator plugin) {

        new CommandAPICommand("thegreatmotivator")
                .withAliases("tgm")
                .withPermission("thegreatmotivator.admin")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        new MultiLiteralArgument("operation", "add", "remove", "set"),
                        new LongArgument("amount", 0, Long.MAX_VALUE)
                )
                .executes((sender, args) -> {
                    OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("player");
                    if (offlinePlayer == null) return;
                    String operationString = (String) args.get("operation");
                    if (operationString == null) return;
                    Long amount = (Long) args.get("amount");
                    if (amount == null) return;

                    Operation operation;
                    try {
                        operation = Operation.valueOf(operationString.toUpperCase(Locale.ENGLISH));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("Invalid operation");
                        return;
                    }

                    CompletableFuture<EconomyResponse> response;

                    switch (operation) {
                        case ADD:
                            response = plugin.addToPlayersBalance(offlinePlayer, amount);
                            break;
                        case REMOVE:
                            response = plugin.withdrawFromPlayersBalance(offlinePlayer, amount);
                            break;
                        case SET:
                            response = plugin.setPlayerBalance(offlinePlayer, amount);
                            break;
                        default:
                            return;
                    }

                    response.thenAccept(economyResponse -> {
                        String name = "Unknown player";
                        if (offlinePlayer.getName() != null) {
                            name = offlinePlayer.getName();
                        }
                        if (economyResponse.isSuccessful()) {
                            sender.sendMessage("%s's balance was successfully modified. Balance after: %s".formatted(name, economyResponse.balance()));
                        } else {
                            sender.sendMessage("Failed to modify %s's balance.".formatted(name));
                        }
                    });
                })
                .register();

        new CommandAPICommand("thegreatmotivator")
                .withAliases("tgm")
                .withPermission("thegreatmotivator.admin")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        new LiteralArgument("check")
                )
                .executes((sender, args) -> {
                    OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("player");
                    if (offlinePlayer == null) return;
                    String name = "Unknown player";
                    if (offlinePlayer.getName() != null) {
                        name = offlinePlayer.getName();
                    }
                    String finalName = name;
                    plugin.getPlayerBalance(offlinePlayer).thenAccept(economyResponse -> sender.sendMessage("%s's balance: %s".formatted(finalName, economyResponse.getBalance())));
                })
                .register();

    }

    enum Operation {
        ADD,
        REMOVE,
        SET,
    }

}
