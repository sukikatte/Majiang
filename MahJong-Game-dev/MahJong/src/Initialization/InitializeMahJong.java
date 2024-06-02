package Initialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import Item.Item;
import Item.WindPlate;
import Item.WordPlate;

//Before the game, Initialization of Mahjong plate
public class InitializeMahJong {
    //牌集合
    private List<Item> mahjongTiles;

    //初始化牌组
    public List<Item> InitializeMahjong() {
        this.mahjongTiles = new ArrayList<>();
        //添加牌组
        initializeMahjongTiles();
        //洗牌返回
        return shuffleMahjongTiles();
    }

    //初始化麻将牌集合
    void initializeMahjongTiles() {
        //添加风牌
        for (int i = 0; i < 4; i++) { // 每种牌四张
            mahjongTiles.add(new WordPlate("东风","dong.PNG",50));
            mahjongTiles.add(new WordPlate("南风","nan.PNG",70));
            mahjongTiles.add(new WordPlate("西风","xi.PNG",60));
            mahjongTiles.add(new WordPlate("北风","bei.PNG",80));
            mahjongTiles.add(new WordPlate("红中","hong.PNG",90));
            mahjongTiles.add(new WordPlate("发财","fa.PNG",100));
            mahjongTiles.add(new WordPlate("白板","bai.PNG",110));
        }

        //添加万字牌
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                mahjongTiles.add(new WordPlate(i + "万",i+"w.PNG",10+i));
            }
        }

        //添加条子牌，其中一条特殊处理为幺鸡
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                mahjongTiles.add(new WordPlate(i + "条",i+"t.PNG",30+i));
            }
        }

        //添加筒子牌
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                mahjongTiles.add(new WordPlate(i + "筒",i+"o.PNG",20+i));
            }
        }

        shuffleMahjongTiles();
    }

    //洗牌方法
    public  List<Item> shuffleMahjongTiles() {
        Collections.shuffle(this.mahjongTiles);
        return this.mahjongTiles;
    }

    //获取麻将牌集合方法
    public List<Item> getMahjongTiles() {
        return mahjongTiles;
    }
    public void setMahjongTiles(List<Item> mahjongTiles) {
        this.mahjongTiles = mahjongTiles;
    }
}
