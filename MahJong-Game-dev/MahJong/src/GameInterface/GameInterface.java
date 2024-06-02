package GameInterface;
import Item.*;
import Player.*;
import Table.*;
import Game.*;
import Initialization.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component

public class GameInterface  {
    //玩家组
    private static List<Player> gameplayers;
    //初始化麻将牌组
    private static InitializeMahJong initializeMahjong;
    //初始化玩家组
    private static InitializePlayer initializePlayer;
    //初始化玩家组
    private static Game game;

    public void init(InitializeMahJong Mahjong,InitializePlayer Player,Table table) throws Exception {
        game = new Game();
        // 生成玩家list
        List<String> playerList = new ArrayList<>();
        playerList.add("WEST");
        playerList.add("SOUTH");
        playerList.add("EAST");
        playerList.add("NORTH");
        initializeMahjong = Mahjong;
        initializePlayer = Player;
        initializeMahjong.InitializeMahjong();     // 洗牌
        initializePlayer.initializePlayers(playerList); // 初始化玩家并分配牌

        table.setWaitinguseTiles(initializeMahjong.getMahjongTiles());
        // 获取初始化完成的玩家列表
        table.getDiscardedTiles().put("WEST", new ArrayList<>());
        table.getDiscardedTiles().put("SOUTH", new ArrayList<>());
        table.getDiscardedTiles().put("EAST", new ArrayList<>());
        table.getDiscardedTiles().put("NORTH", new ArrayList<>());
        gameplayers = initializePlayer.getPlayers();
    }

    //绘画区域
    public BorderPane drawingInterface(BorderPane borderPane, Stage primaryStage) {
        // 创建总布局面板
        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: cornflowerblue;"); // 设置背景色为矢车菊蓝
        // 创建四周的牌区和打出的牌区
        VBox west = createVerticalPlayerArea(gameplayers.get(3), 90, null);
        HBox south = createHorizontalPlayerArea(gameplayers.get(1), 0, null);
        VBox east = createVerticalPlayerArea(gameplayers.get(2), -90, null);
        HBox north = createHorizontalPlayerArea(gameplayers.get(0), 180, null); // 北方玩家
        // 将牌区和打出的牌区添加到边界布局对应的位置
        borderPane.setLeft(west);
        borderPane.setBottom(south);
        borderPane.setRight(east);
        borderPane.setTop(north);

        // 设置场景和舞台
        Scene scene = new Scene(borderPane, 900, 600);
        primaryStage.setTitle("麻将游戏布局");
        primaryStage.setScene(scene);
        primaryStage.show();
        return borderPane;
    }

    // 辅助方法：玩家牌区
    private HBox createHorizontalPlayerArea(Player player, double rotationAngle, Item newitem) {
        HBox hbox = new HBox(3);
        String file = "";

        // 设置路径，根据旋转角度
        if (rotationAngle == 180) {
            file = "/resources/static/north/";
        } else if (rotationAngle == 0) {
            file = "/resources/static/";
        }

        // 处理玩家的每一个麻将牌
        for (Item item : player.getPlayerMahjong()) {
            String url = "";
            if (!player.getName().equals("SOUTH")) {
                url = "bj.PNG"; // 对于非南方向的玩家，使用 `bj.PNG`
            } else {
                url = item.getImgurl();
            }

            // 判断麻将牌类型
            ImageView imageView = new ImageView(new Image(getClass().getResource(file + url).toString()));
            if (item.getType().equals("new")) {
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,51,1), 30, 0, 0, 0);");
                imageView.setOnMouseClicked(event -> game.handleTileClick(player, newitem));
                item.setType("old");
            } else {
                imageView.setOnMouseClicked(event -> game.handleTileClick(player, item));
            }

            // 设置图像的高度和宽度
            imageView.setFitHeight(45);
            imageView.setFitWidth(30);
            hbox.getChildren().add(imageView);
        }

        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    // 辅助方法创建东西方向玩家牌区，并应用旋转
    private VBox createVerticalPlayerArea(Player player, double rotationAngle, Item newitem) {
        VBox vbox = new VBox(3);
        String file = "";

        if (rotationAngle == -90) {
            file = "/resources/static/east/";
        } else if (rotationAngle == 90) {
            file = "/resources/static/west/";
        }

        for (Item item : player.getPlayerMahjong()) {
            String url;
            if (!player.getName().equals("SOUTH")) {
                url = "bj.PNG";
            } else {
                url = item.getImgurl();
            }

            ImageView imageView = new ImageView(new Image(getClass().getResource(file + url).toString()));
            if (item.getType().equals("new")) {
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,51,1), 30, 0, 0, 0);");
                imageView.setOnMouseClicked(event -> game.handleTileClick(player, newitem));
                item.setType("old");
            } else {
                imageView.setOnMouseClicked(event -> game.handleTileClick(player, item));
            }

            // 设置图像的高度和宽度
            imageView.setFitHeight(30);
            imageView.setFitWidth(45);
            vbox.getChildren().add(imageView);
        }

        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

}
