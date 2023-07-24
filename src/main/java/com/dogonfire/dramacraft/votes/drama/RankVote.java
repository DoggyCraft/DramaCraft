package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class RankVote extends Vote {
    public UUID targetPlayerId = null;

    public RankVote(String voteType, World world, Player voter, String voteText, boolean vote) {
        super(voteType, world, voter, voteText, vote);
        targetPlayerId = UUID.fromString(voteText);
        this.voteText = Bukkit.getServer().getOfflinePlayer(targetPlayerId).getName();
    }

    public RankVote(String voteType, float reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
        super(voteType, reqYesPercentage, reqVotes, voteCost, votePayment, world, voter, voteText, vote);
        targetPlayerId = UUID.fromString(voteText);
        this.voteText = Bukkit.getServer().getOfflinePlayer(targetPlayerId).getName();
    }

    public RankVote(String voteType, double reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
        this(voteType, ((float) reqYesPercentage)/100f, reqVotes, voteCost, votePayment, world, voter, voteText, vote);
    }

    @Override
    public void broadcastVote() {
        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(targetPlayerId);
        LanguageManager.setPlayerName(player.getName());
        DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType), ChatColor.AQUA));
    }
}
