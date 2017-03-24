package samurai.entities;

import org.json.JSONObject;
import samurai.Bot;
import samurai.command.Commands;
import samurai.osu.entities.Score;
import samurai.osu.enums.GameMode;

import java.io.*;
import java.time.Instant;
import java.util.*;

/**
 * manages primary specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SamuraiGuild {

    private String prefix;
    private long guildId;
    private transient boolean active;
    private transient HashMap<String, LinkedList<Score>> scoreMap;
    private ArrayList<SamuraiUser> users;
    private ArrayList<Chart> charts;
    private long enabledCommands;
    private long dedicatedChannel;
    private HashMap<Long, GameMode> channelModes;

    public SamuraiGuild() {
    }


    public SamuraiGuild(long guildId) {
        prefix = Bot.DEFAULT_PREFIX;
        this.guildId = guildId;
        scoreMap = new HashMap<>();
        users = new ArrayList<>();
        charts = new ArrayList<>();
        active = false;
        enabledCommands = Commands.getDefaultEnabledCommands();
        dedicatedChannel = 0L;
        channelModes = new HashMap<>();
    }

    public static int mergeScoreMap(HashMap<String, LinkedList<Score>> base, HashMap<String, LinkedList<Score>> annex) {
        int scoresMerged = 0;
        for (Map.Entry<String, LinkedList<Score>> sourceEntry : annex.entrySet()) {
            if (base.containsKey(sourceEntry.getKey())) {
                List<Score> destinationScores = base.get(sourceEntry.getKey());
                for (Score sourceScore : sourceEntry.getValue())
                    if (!destinationScores.contains(sourceScore)) {
                        destinationScores.add(sourceScore);
                        scoresMerged++;
                    }
            } else {
                base.put(sourceEntry.getKey(), sourceEntry.getValue());
                scoresMerged += sourceEntry.getValue().size();
            }
        }
        return scoresMerged;
    }

    public void addUser(long id, JSONObject userJSON) {
        users.add(new SamuraiUser(id, userJSON.getInt("user_id"), userJSON.getString("username"), userJSON.getInt("pp_rank"), userJSON.getInt("pp_country_rank"), Instant.now().getEpochSecond()));
        updateLocalRanks();

    }

    public SamuraiUser getUser(long discordId) {
        for (SamuraiUser user : users)
            if (user.getDiscordId() == discordId) return user;
        return null;
    }

    private void updateLocalRanks() {
        users.sort(Comparator.comparingInt(SamuraiUser::getG_rank));
        for (int i = 1; i <= users.size(); i++) {
            users.get(i - 1).setL_rank((short) i);
        }
    }

    public boolean hasUser(long id) {
        for (SamuraiUser s : users)
            if (s.getDiscordId() == id)
                return true;
        return false;
    }

    public int getUserCount() {
        return users.size();
    }

    public HashMap<String, LinkedList<Score>> getScoreMap() {
        return scoreMap;
    }

    public void setScoreMap(HashMap<String, LinkedList<Score>> scoreMap) {
        if (scoreMap != null)
            this.scoreMap = scoreMap;
    }

    public int getScoreCount() {
        return scoreMap.values().stream().mapToInt(LinkedList::size).sum();
    }

    public int getScoreCount(String name) {
        return (int) scoreMap.values().stream().flatMap(Collection::stream).filter(s -> s.getPlayer().equals(name)).count();
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        this.active = false;
    }

    public ArrayList<SamuraiUser> getUsers() {
        return users;
    }

    public long getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        active = true;
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SamuraiGuild that = (SamuraiGuild) o;

        return guildId == that.guildId;
    }

    @Override
    public int hashCode() {
        return (int) (guildId ^ (guildId >>> 32));
    }


    public byte[] writeBytes() throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bout);
        out.writeUTF(prefix);
        out.writeLong(guildId);
        out.writeLong(enabledCommands);
        out.writeLong(dedicatedChannel);

        out.writeShort(users.size());
        for (SamuraiUser u : users) {
            out.writeLong(u.getDiscordId());
            out.writeInt(u.getOsuId());
            out.writeUTF(u.getOsuName());
            out.writeInt(u.getG_rank());
            out.writeInt(u.getC_rank());
            out.writeShort(u.getL_rank());
            out.writeLong(u.getLastUpdated());
        }

        out.writeShort(charts.size());
        for (Chart c : charts) {
            out.writeInt(c.getChartId());
            out.writeUTF(c.getChartName());
            out.writeByte(c.getBeatmapIds().size());
            for (int i : c.getBeatmapIds())
                out.writeInt(i);
        }

        out.writeShort(channelModes.size());
        for (Map.Entry<Long, GameMode> entry : channelModes.entrySet()) {
            out.writeLong(entry.getKey());
            out.writeByte(entry.getValue().value());
        }
        return bout.toByteArray();
    }


    public boolean readBytes(byte[] data) {
        try {
            final ByteArrayInputStream bin = new ByteArrayInputStream(data);
            final DataInputStream in = new DataInputStream(bin);
            prefix = in.readUTF();
            guildId = in.readLong();
            enabledCommands = in.readLong();
            dedicatedChannel = in.readLong();
            users = new ArrayList<>();
            for (int i = 0, size = in.readShort(); i < size; i++) {
                long id = in.readLong();
                users.add(new SamuraiUser(id, in.readInt(), in.readUTF(), in.readInt(), in.readInt(), in.readShort(), in.readLong()));
            }
            charts = new ArrayList<>();
            for (int i = 0, size = in.readShort(); i < size; i++) {
                int id = in.readInt();
                String name = in.readUTF();
                int chartSize = in.readByte();
                ArrayList<Integer> chart = new ArrayList<>(chartSize);
                for (int j = 0; j < chartSize; j++) {
                    chart.add(in.readInt());
                }
                charts.add(new Chart(id, name, chart));
            }

            channelModes = new HashMap<>();
            for (int i = 0, size = in.readShort(); i < size; i++) {
                channelModes.put(in.readLong(), GameMode.get(in.readByte()));
            }

            scoreMap = new HashMap<>();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("SamuraiGuild{%n\tprefix='%s'%n\tguildId=%d%n\tscoreCount=%d%n\tusers=%n%s%n\tcharts=%s%n\tactive=%s%n}", prefix, guildId, getScoreCount(), users, charts, active);
    }

    public long getEnabledCommands() {
        return enabledCommands;
    }

    public void setEnabledCommands(long enabledCommands) {
        this.enabledCommands = enabledCommands;
    }

    public long getDedicatedChannel() {
        return dedicatedChannel;
    }

    public void setDedicatedChannel(String dedicatedChannel) {
        this.dedicatedChannel = Long.parseLong(dedicatedChannel);
    }

    public GameMode getMode(long channelId) {
        return channelModes.getOrDefault(channelId, GameMode.OSU);
    }

    public void setMode(long channelId, GameMode mode) {
        this.channelModes.put(channelId, mode);
    }
}
