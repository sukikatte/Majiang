package Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import Item.Item;
import Table.Table;

//Player's relative regulations
public class Player {
    // 玩家名称
    private String name;
    // 玩家手中的麻将牌
    private List<Item> playerMahjong;
    // 游戏中的桌面类
    private Table table;
    //积分
    private int score;

    // 构造函数
    public Player(String name) {
        this.name = name;
        this.playerMahjong = new ArrayList<>();
        this.table = table;
        this.score = 0;
    }

    // 玩家摸牌
    public void takeMahjong(Item mahjong) {
        playerMahjong.add(mahjong);
    }

    // 玩家打出牌
    public void removeMahjong(Item mahjong) {
        playerMahjong.remove(mahjong);
    }

    // 获取玩家的名字
    public String getName() {
        return name;
    }

    // 获取玩家的麻将牌
    public List<Item> getPlayerMahjong() {
        return playerMahjong;
    }

    // 获取玩家的分数
    public int getScore() {
        return score;
    }

    // 设置玩家的分数
    public void setScore(int score) {
        this.score = score;
    }

    // 打印玩家手中的麻将牌，用于调试和展示
    public void displayTiles() {
        System.out.println(name + "'s Tiles:");
        for (Item tile : playerMahjong) {
            System.out.println(tile.getName());
        }
    }





    // 假设Item类已经有equals方法来比较牌是否相同
    public boolean canPong(Item tile) {
        int count = 0;
        for (Item t : playerMahjong) {
            if (t.equals(tile)) {
                count++;
            }
        }
        return count >= 2;
    }

    // 玩家执行暗杠操作
    public void concealedKong(Item tile) {
        int count = Collections.frequency(playerMahjong, tile);
        if (count == 4) {  // 检查手中是否有四张相同的牌
            performKong(tile);
        }
    }

    // 玩家执行明杠操作
    public void exposedKong(Item tile) {
        if (table.canKong(tile, this)) {  // 检查桌面上是否可以进行明杠
            performKong(tile);
        }
    }

    // 执行杠操作，移除手牌并更新桌面
    private void performKong(Item tile) {
        List<Item> kongTiles = new ArrayList<>();
        Iterator<Item> iterator = playerMahjong.iterator();
        while (iterator.hasNext()) {
            Item t = iterator.next();
            if (t.equals(tile)) {
                kongTiles.add(t);
                iterator.remove();
                if (kongTiles.size() == 4) {
                    break;
                }
            }
        }
        kongTiles.add(tile); // 添加最近打出或摸到的牌
        table.addShownTiles(kongTiles);
    }

    // 玩家执行吃操作
    public void eat(Item tile) {
        int tileNumber = extractNumber(tile.getName());
        String tileType = extractType(tile.getName());
        List<Item> chowTiles = findMatchingTilesForChow(tileNumber, tileType);
        // 如果可以形成顺子，则移除手牌中相应的牌，并更新桌面
        if (chowTiles.size() == 2) {
            playerMahjong.removeAll(chowTiles);
            chowTiles.add(tile); // 添加打出的牌
            table.addShownTiles(chowTiles);
        }
    }

    // 查找是否存在与给定牌组成顺子的牌
    private List<Item> findMatchingTilesForChow(int tileNumber, String tileType) {
        List<Item> matchingTiles = new ArrayList<>();
        int[] neededNumbers = {tileNumber - 1, tileNumber + 1}; // 需要的牌号，构成顺子

        for (int number : neededNumbers) {
            if (number > 0 && number < 10) { // 确保牌号有效
                for (Item handTile : playerMahjong) {
                    if (extractNumber(handTile.getName()) == number && extractType(handTile.getName()).equals(tileType)) {
                        matchingTiles.add(handTile);
                        if (matchingTiles.size() == 2) {
                            return matchingTiles;
                        }
                    }
                }
            }
        }
        return matchingTiles;
    }

    private String extractType(String name) {
        return name.replaceAll("[0-9]", "");
    }

    private int extractNumber(String name) {
        return Integer.parseInt(name.replaceAll("\\D+", ""));
    }

    // 玩家执行碰操作
    public void pong(Item tile) {
        //创建一个新的列表，用来存放即将展示在桌面上的三张牌（两张来自手牌，一张是打出的牌）
        List<Item> pongTiles = new ArrayList<>();
        //用来计数，确保从手中移除的相同牌的数量不超过两张
        int count = 0;
        //迭代手牌:
        for (Iterator<Item> iterator = playerMahjong.iterator(); iterator.hasNext();) {
            Item t = iterator.next();
            //如果找到相同的牌，并且之前没有找到超过两张（count < 2），则从手牌中移除这张牌，并将其添加到 pongTiles 列表中，同时增加 count 的值
            if (t.equals(tile) && count < 2) {
                iterator.remove();
                pongTiles.add(t);
                count++;
            }
        }
        // 添加最近打出的牌
        pongTiles.add(tile);
        table.addShownTiles(pongTiles);
    }
}


