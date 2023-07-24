package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class RebelRankVote extends RankVote {
    public RebelRankVote(String voteType, World world, Player voter, String voteText, boolean vote) {
        super(voteType, world, voter, voteText, vote);
    }

    public RebelRankVote(String voteType, float reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
        super(voteType, reqYesPercentage, reqVotes, voteCost, votePayment, world, voter, voteText, vote);
    }

    public RebelRankVote(String voteType, double reqYesPercentage, int reqVotes, int voteCost, int votePayment, World world, Player voter, String voteText, boolean vote) {
        this(voteType, ((float) reqYesPercentage)/100f, reqVotes, voteCost, votePayment, world, voter, voteText, vote);
    }

    @Override
    public void broadcastVote() {
        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(targetPlayerId);
        LanguageManager.setPlayerName(player.getName());
        DramaCraft.broadcastToRebels(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType), ChatColor.AQUA, player.getName());
        DramaCraft.broadcastToRebels(LanguageManager.LANGUAGESTRING.VOTE_BROADCAST_REBEL_HIDDEN, ChatColor.GRAY, player.getName());
    }

    @Override
    public boolean enoughOnlinePlayers() {
        if(RankManager.getOnlineRebels() < reqVotes)
        {
            LanguageManager.setAmount1(reqVotes);
            voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWREBELS_ONLINE, ChatColor.RED));
            DramaCraft.logDebug(voter.getName() + " tried to vote for a rebel rank, but there are too few rebel players online");
            return false;
        }
        return true;
    }

    @Override
    public boolean fulfillsVoteRequirement(Player votingPlayer) {
        if(!RankManager.isRebel(votingPlayer.getUniqueId()))
        {
            votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANBEINNERCIRCLE, ChatColor.RED));
            return false;
        }
        return true;
    }
}

