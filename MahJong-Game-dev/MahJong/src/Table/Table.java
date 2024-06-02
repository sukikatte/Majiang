package Table;

import Item.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Player.Player;



public class Table {
    // 存储吃、碰、杠的牌组
    private List<List<Item>> shownTiles;
    // 存储打出但未处理的牌
    private Map<String,List<Item>> discardedTiles;
    // 上个玩家打出的牌
    private Item lastTile;

    public void setShownTiles(List<List<Item>> shownTiles) {
        this.shownTiles = shownTiles;
    }

    public void setDiscardedTiles(Map<String,List<Item>>  discardedTiles) {
        this.discardedTiles = discardedTiles;
    }

    public List<Item> getWaitinguseTiles() {
        return waitinguseTiles;
    }

    public void setWaitinguseTiles(List<Item> waitinguseTiles) {
        this.waitinguseTiles = waitinguseTiles;
    }

    //待摸牌组
    private  List<Item> waitinguseTiles;


    public Table() {
        this.shownTiles = new ArrayList<>();
        this.discardedTiles = new HashMap<>();
        this.lastTile = null;
    }

    // 添加新的牌组到桌面
    public void addShownTiles(List<Item> tiles) {
        this.shownTiles.add(tiles);
    }

    // 设置上一张打出的牌
    public void setLastTile(Item tile) {
        this.lastTile = tile;
    }

    // 获取上一张打出的牌
    public Item getLastTile() {
        return lastTile;
    }

    // 记录被打出但未被处理的牌
    public void addDiscardedTile(String string , Item tile) {
        this.discardedTiles.get(string).add(tile);
    }

    // 获取所有打出但未被处理的牌
    public Map<String,List<Item>> getDiscardedTiles() {
        return discardedTiles;
    }

    // 获取显示在桌面上的所有牌组
    public List<List<Item>> getShownTiles() {
        return shownTiles;
    }


    // 检查是否可以对指定的牌进行明杠
    public boolean canKong(Item tile, Player player) {
        for (List<Item> tiles : shownTiles) {
            // 检查这组牌是否属于这个玩家
            if (tiles.contains(tile) && countTiles(tiles, tile) == 3) {
                // 确保这组牌是由玩家展示的并且包含三张相同的牌
                return true;
            }
        }
        return false; // 如果没有找到相符的三张牌组，返回false
    }

    // 辅助方法：计数在某个牌组中特定牌的数量
    private int countTiles(List<Item> tiles, Item targetTile) {
        int count = 0;
        for (Item tile : tiles) {
            if (tile.equals(targetTile)) {
                count++;
            }
        }
        return count;
    }
}
