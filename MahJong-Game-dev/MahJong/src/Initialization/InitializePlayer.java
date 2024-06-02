package Initialization;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import Item.Item;
import Player.Player;

//Before the game, Initialization of the player
public class InitializePlayer {
    //麻将组对象
    private final InitializeMahJong initializeMahjong;
    //玩家列表
    private List<Player> players;

    public InitializePlayer(InitializeMahJong initializeMahjong) {
        this.initializeMahjong = initializeMahjong;
        this.players = new ArrayList<>();
    }

    // 获取所有玩家
    public List<Player> getPlayers() {
        return players;
    }

    // 初始化玩家，并发放初始牌
    public  List<Item> initializePlayers(List<String> playerNames) {
        List<Item> tiles = initializeMahjong.getMahjongTiles();

        // 根据麻将规则，每个玩家初始13张牌
        int numTilesPerPlayer = 13;

        for (String name : playerNames) {
            Player player = new Player(name);

            // 从牌堆中取牌并发给玩家
            for (int i = 0; i < numTilesPerPlayer; i++) {
                if (!tiles.isEmpty()) {
                    player.takeMahjong(tiles.remove(0));
                }
            }
            Collections.sort(player.getPlayerMahjong());



            players.add(player);
        }

//        players.add(1, player2);
        return tiles;
    }

}
