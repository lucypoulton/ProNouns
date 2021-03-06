package net.lucypoulton.pronouns.discord;

import net.lucypoulton.pronouns.ProNouns;
import net.lucypoulton.pronouns.ProNounsPlatform;
import net.lucypoulton.pronouns.storage.MysqlConnectionException;
import net.lucypoulton.pronouns.storage.MysqlFileStorage;
import net.lucypoulton.pronouns.storage.Storage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class ProNounsDiscordStandalone {

    private final Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public ProNounsDiscordStandalone() throws LoginException, MysqlConnectionException, IOException {
        TestConfigHandler configHandler = new TestConfigHandler();
        JDA jda = JDABuilder.createDefault(configHandler.getDiscordToken())
            .setChunkingFilter(ChunkingFilter.ALL)
            .setActivity(Activity.listening(configHandler.getCommandPrefix() + "pronouns - ProNouns for Discord Beta"))
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .build();
        ProNounsPlatform platform = new ProNounsDiscordPlatform(jda, configHandler, this);

        ProNouns plugin = new ProNounsDiscord(platform, jda);
        storage = new MysqlFileStorage(plugin);
        plugin.onEnable();
    }

    public static void main(String[] args) throws LoginException, MysqlConnectionException, IOException {
        new ProNounsDiscordStandalone();
    }
}
