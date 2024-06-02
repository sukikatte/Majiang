package Game;

import Item.Item;
import Player.Player;
import Dice.Dice;
import GameInterface.*;
import Initialization.InitializeMahJong;
import Initialization.InitializePlayer;
import Table.Table;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game extends Application {
    private Stage primaryStage;

    private GameInterface gameinterface;

    //初始化麻将牌组
    private InitializeMahJong initializeMahjong;
    //初始化玩家组
    private InitializePlayer initializePlayer;
    //玩家组
    private static List<Player> players;
    //桌面类
    private static Table table;
    //吃碰杠牌组
    private static List<Item> peng;
    //当前玩家下标
    private int currentPlayerIndex = 1;
    //布局对象
    private static BorderPane borderPane;

    //从牌堆摸一张牌
    private Item drawCard(Player player) {
        List<Item> remainingTiles = table.getWaitinguseTiles();
        if (!remainingTiles.isEmpty()) {
            Item drawnCard = remainingTiles.remove(0);  // 从牌堆中移除一张牌并返回
            drawnCard.setType("new");
            player.takeMahjong(drawnCard);
            // 将这张牌添加到玩家手牌中
            return drawnCard;
        }
        //如果为空显示排行榜结束
        int size = players.get(0).getPlayerMahjong().size() + players.get(1).getPlayerMahjong().size() + players.get(2).getPlayerMahjong().size() + players.get(3).getPlayerMahjong().size();
        // 假设 table 是一个 Map 对象，键为某种类型，值为 List 对象
        for (List<Item> list : table.getDiscardedTiles().values()) {
            size = list.size() + size;
        }
        size = size + peng.size();
        System.out.println("List size: " + size);
        // 创建一个警告对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("牌打完了！");
        alert.setHeaderText("玩家排行榜");
        Label label = new Label("\n张三:   100分\n李四:   90分\n王五:   80分\n赵六:   100分\n田七:   90分\nXX:    80分");
        label.setWrapText(true); // 设置文本自动换行
        alert.getDialogPane().setContent(label);
        alert.getDialogPane().setPrefSize(400, 700); // 设置宽度和高度
        Stage stage = (Stage) borderPane.getScene().getWindow();
        // 添加按钮
        ButtonType buttonTypeOne = new ButtonType("再来一局");
        ButtonType buttonTypeTwo = new ButtonType("退出游戏");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        // 显示对话框并等待响应
        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOne) {
            //初始化麻将牌组
            initializeMahjong = null;
            //初始化玩家组
            initializePlayer = null;
            //玩家组
            players = null;
            //桌面类
            table = null;
            //吃碰杠牌组
            peng = null;
            //当前玩家下标
            currentPlayerIndex = 1;
            //布局对象
            BorderPane borderPane = null;
        } else {
            // 用户选择退出游戏
            stage.close();
            System.exit(0);
            start(primaryStage);
        }
        start(primaryStage);
        return null;
    }

    @Override
    public void start(Stage primaryStage) {
        //存储框架入口参数
        this.primaryStage = primaryStage;
        //调用登录界面
        login(primaryStage, this::onLoginSuccess);
    }

    //点击登录以后处理的逻辑函数
    public void onLoginSuccess() {
        initializeMahjong = new InitializeMahJong();
        initializePlayer = new InitializePlayer(initializeMahjong);
        gameinterface = new GameInterface();
        peng = new ArrayList<>();
        table = new Table();
        try {
            gameinterface.init(initializeMahjong, initializePlayer, table);
        } catch (Exception e) {

        }
        players = initializePlayer.getPlayers();
        borderPane = gameinterface.drawingInterface(new BorderPane(), primaryStage);
        nextTurn(true);
    }

    //处理下一回合
    private void nextTurn(Boolean code) {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer.getName().equals("SOUTH")) {
            Collections.sort(currentPlayer.getPlayerMahjong());
            handleHumanPlayer(currentPlayer, code);
        } else {
            handleAIPlayer(currentPlayer);
            System.out.println(currentPlayer.getName());
            Collections.sort(currentPlayer.getPlayerMahjong());
        }
    }

    //玩家回合处理
    private void handleHumanPlayer(Player player, Boolean code) {
        Platform.runLater(() -> {
            Item drawnCard = null;
            List<Item> list = new ArrayList<>(players.get(1).getPlayerMahjong());
            //如果进行了碰吃则不摸牌
            if (code) {
                drawnCard = drawCard(player);
                if (drawnCard == null) {
                    return;
                }
                list.add(drawnCard);
                if (isWin(list)) {
                    System.out.println("自己赢了摸到了" + drawnCard.getName());
                    // 创建一个警告对话框
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("恭喜你赢了！");
                    alert.setHeaderText("玩家排行榜");
                    Label label = new Label("\n张三:   100分\n李四:   90分\n王五:   80分\n赵六:   100分\n田七:   90分\nXX:    80分");
                    label.setWrapText(true); // 设置文本自动换行
                    alert.getDialogPane().setContent(label);
                    alert.getDialogPane().setPrefSize(400, 700); // 设置宽度和高度
                    Stage stage = (Stage) borderPane.getScene().getWindow();

                    // 添加按钮
                    ButtonType buttonTypeOne = new ButtonType("再来一局");
                    ButtonType buttonTypeTwo = new ButtonType("退出游戏");
                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                    // 显示对话框并等待响应
                    java.util.Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeOne) {
                        initializeMahjong = null;
                        initializePlayer = null;
                        players = null;
                        table = null;
                        peng = null;
                        currentPlayerIndex = 1;
                        //布局对象
                        BorderPane borderPane = null;
                    } else {
                        //选择退出游戏
                        stage.close();
                        System.exit(0);
                        return;
                    }
                    start(primaryStage);
                    return;
                }
                //处理暗杠
                if (ancanGangpen(player, drawnCard)) {
                    if (anhandleGengpen(player, drawnCard)) {
                        nextTurn(true);
                    }
                }
                if (ancanGang(player, drawnCard)) {
                    if (anhandleGeng(player, drawnCard)) {
                        nextTurn(true);
                    }
                }
            }

            if (isWin(list)) {
                System.out.println("自己赢了摸到了" + drawnCard.getName());
                Stage stage = (Stage) borderPane.getScene().getWindow();
                // 创建一个警告对话框
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("恭喜你赢了！");
                alert.setHeaderText("玩家排行榜");
                Label label = new Label("\n张三:   100分\n李四:   90分\n王五:   80分\n赵六:   100分\n田七:   90分\nXX:    80分");
                label.setWrapText(true); // 设置文本自动换行
                alert.getDialogPane().setContent(label);
                alert.getDialogPane().setPrefSize(400, 700); // 设置宽度和高度

                ButtonType buttonTypeOne = new ButtonType("再来一局");
                ButtonType buttonTypeTwo = new ButtonType("退出游戏");
                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                // 显示对话框并等待响应
                java.util.Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeOne) {
                    initializeMahjong = null;
                    initializePlayer = null;
                    players = null;
                    table = null;
                    peng = null;
                    currentPlayerIndex = 1;
                    //布局对象
                    BorderPane borderPane = null;
                } else {
                    //选择退出游戏
                    stage.close();
                    System.exit(0);
                    return;
                }
                start(primaryStage);
                return;
            }
            //刷新手牌，打出牌，以及碰吃杠牌
            refreshUI(player, drawnCard, null);
        });
    }

    // 辅助方法：去除牌名中的中文字符
    private String removeChineseCharacters(String cardName) {
        return cardName.replaceAll("[\u4e00-\u9fa5]", "");
    }

    // 检查是否可以吃
    private Map<String, List<Item>> canChi(Player player, Item card) {
        List<Item> hand = player.getPlayerMahjong();
        // 这里假设 Item 类有 getName 方法返回牌的名称
        String cardName = removeChineseCharacters(card.getName());
        // 使用正则表达式匹配数字和汉字部分
        String regex = "(\\d+)([\\u4e00-\\u9fa5]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(card.getName());

        if (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            String chinesePart = matcher.group(2);
            System.out.println("数字: " + number);
            System.out.println("汉字部分: " + chinesePart);
            String previousCardName = "";
            String nextCardName = "";
            Item hasPreviousItem = null;
            Item hasNextItem = null;
            boolean hasPreviousCard = false;
            boolean hasNextCard = false;
            //判断中间吃的逻辑
            if (number > 1 && number < 9) {
                String preNumber = String.valueOf(number - 1);
                String nextNumber = String.valueOf(number + 1);
                previousCardName = preNumber + chinesePart;
                nextCardName = nextNumber + chinesePart;

                for (Item item : hand) {
                    if (item.getName().equals(previousCardName)) {
                        hasPreviousItem = item;
                        hasPreviousCard = true;
                    }
                    if (item.getName().equals(nextCardName)) {
                        hasNextItem = item;
                        hasNextCard = true;
                    }
                }
            }
            Item NextItem = null;
            Item NextnextItem = null;
            boolean NextCard = false;
            boolean NextnextCard = false;
            if (number < 8) {
                String preNumber = String.valueOf(number + 1);
                String nextNumber = String.valueOf(number + 2);
                previousCardName = preNumber + chinesePart;
                nextCardName = nextNumber + chinesePart;
                for (Item item : hand) {
                    if (item.getName().equals(previousCardName)) {
                        NextItem = item;
                        NextCard = true;
                    }
                    if (item.getName().equals(nextCardName)) {
                        NextnextItem = item;
                        NextnextCard = true;
                    }
                }
            }
            boolean PreCard = false;
            boolean PrePreCard = false;
            Item PreItem = null;
            Item PrePreItem = null;
            if (number > 2) {
                String preNumber = String.valueOf(number - 1);
                String nextNumber = String.valueOf(number - 2);
                previousCardName = preNumber + chinesePart;
                nextCardName = nextNumber + chinesePart;
                for (Item item : hand) {
                    if (item.getName().equals(previousCardName)) {
                        PreItem = item;
                        PreCard = true;
                    }
                    if (item.getName().equals(nextCardName)) {
                        PrePreItem = item;
                        PrePreCard = true;
                    }
                }
            }
            List<Item> hasPreviousCarditems = new ArrayList<>();
            hasPreviousCarditems.add(hasPreviousItem);
            hasPreviousCarditems.add(hasNextItem);
            List<Item> PreCarditems = new ArrayList<>();
            PreCarditems.add(PreItem);
            PreCarditems.add(PrePreItem);
            List<Item> NextCarditems = new ArrayList<>();
            NextCarditems.add(NextItem);
            NextCarditems.add(NextnextItem);
            Map<String, List<Item>> map = new HashMap<>();
            if (PreCard && PrePreCard) {
                map.put("Pre", PreCarditems);
            }
            if (NextCard && NextnextCard) {
                map.put("Next", NextCarditems);
            }
            if (hasPreviousCard && hasNextCard) {
                map.put("Has", hasPreviousCarditems);
            }
            return map;
        } else {
            return null;
        }
    }

    // 检查是否可以碰
    private boolean canPeng(Player player, Item card) {
        List<Item> hand = player.getPlayerMahjong();
        // 检查手牌中是否有两张与 card 相同的牌
        int count = 0;
        for (Item item : hand) {
            if (item.getName().equals(card.getName())) {
                count++;
            }
        }
        return count >= 2;
    }

    // 检查是否可以杠
    private boolean canGang(Player player, Item card) {
        List<Item> hand = player.getPlayerMahjong();
        // 检查手牌中是否有三张与 card 相同的牌
        int count = 0;
        for (Item item : hand) {
            if (item.getName().equals(card.getName())) {
                count++;
            }
        }
        return count >= 3;
    }

    // 检查是否可以杠
    private boolean ancanGang(Player player, Item card) {
        List<Item> hand = new ArrayList<>(player.getPlayerMahjong());
        // 检查手牌中是否有三张与 card 相同的牌
        int count = 0;
        if (hand.size() == 0) {
            return false;
        }
        for (Item item : hand) {
            if (item.getName().equals(card.getName())) {
                count++;
            }
        }
        return count >= 4;
    }

    // 检查是否可以杠
    private boolean ancanGangpen(Player player, Item card) {
        List<Item> hand = new ArrayList<>(peng);
        // 检查手牌中是否有三张与 card 相同的牌
        int count = 0;
        if (hand.size() == 0) {
            return false;
        }
        for (Item item : hand) {
            if (item.getName().equals(card.getName())) {
                count++;
            }
        }
        return count >= 3;
    }

    //处理碰
    private boolean handlePeng(Player player, Item card) {
        //创建提示窗口
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText("是否需要碰牌：" + card.getName());
        ButtonType buttonTypePeng = new ButtonType("碰");
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypePeng);
        // 添加响应处理程序
        dialog.setOnCloseRequest(event -> {
            // 处理关闭提示框操作
            System.out.println("取消了碰");
        });
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypePeng) {
            System.out.println("用户点击了'碰'按钮");
            //先移除打出玩家的手牌
            player.removeMahjong(card);
            List<Item> itemList = players.get(1).getPlayerMahjong();
            // 遍历并删除名称为"Alice"的元素
            int count = 0;
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).getName().equals(card.getName())) {
                    //移除手牌并且添加到碰牌组
                    itemList.remove(i);
                    count++;
                    if (count == 2) {
                        break;
                    }
                }
            }
            peng.add(card);
            peng.add(card);
            peng.add(card);
            //刷新UI
            refreshUI(player, null, card);
            //进入我的回合，并且设置不抽牌
            return true;
        } else {
            return false;
        }
    }

    //处理碰
    private boolean handleGeng(Player player, Item card) {
        //创建提示窗口
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText("是否需要杠牌：" + card.getName());
        ButtonType buttonTypePeng = new ButtonType("杠");
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypePeng);

        // 添加响应处理程序
        dialog.setOnCloseRequest(event -> {
            // 处理关闭提示框操作
            System.out.println("取消了杠");
        });
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypePeng) {
            System.out.println("用户点击了'杠'按钮");
            //先移除打出玩家的手牌
            player.removeMahjong(card);
            List<Item> itemList = players.get(1).getPlayerMahjong();
            // 遍历并删除名称为"Alice"的元素
            int count = 0;
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).getName().equals(card.getName())) {
                    //移除手牌并且添加到碰牌组
                    itemList.remove(i);
                    count++;
                    if (count == 3) {
                        break;
                    }
                }
            }
            peng.add(card);
            peng.add(card);
            peng.add(card);
            peng.add(card);
            //刷新UI
            refreshUI(player, null, card);
            //进入我的回合，并且设置不抽牌
            return true;
        } else {
            return false;
        }
    }

    //处理碰
    private boolean anhandleGeng(Player player, Item card) {
        //创建提示窗口
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText("是否需要暗杠牌：" + card.getName());
        ButtonType buttonTypePeng = new ButtonType("an杠");
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypePeng);

        // 添加响应处理程序
        dialog.setOnCloseRequest(event -> {
            // 处理关闭提示框操作
            System.out.println("取消了杠");
        });
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypePeng) {
            System.out.println("用户点击了'杠'按钮");
            //先移除打出玩家的手牌
            List<Item> itemList = players.get(1).getPlayerMahjong();
            // 遍历并删除名称为"Alice"的元素
            int count = 0;
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).getName().equals(card.getName())) {
                    //移除手牌并且添加到碰牌组
                    itemList.remove(i);
                    count++;
                    if (count == 4) {
                        break;
                    }
                }
            }
            peng.add(card);
            peng.add(card);
            peng.add(card);
            peng.add(card);
            //刷新UI
            refreshUI(player, null, card);
            //进入我的回合，并且设置不抽牌
            return true;
        } else {
            return false;
        }
    }

    private boolean handleChi(Player player2, Item card, Map<String, List<Item>> map) {
        // 创建提示窗口
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText("请选择吃牌的方式：" + card.getName());

        // 根据 map 动态创建按钮
        DialogPane dialogPane = dialog.getDialogPane();
        for (Map.Entry<String, List<Item>> entry : map.entrySet()) {
            ButtonType buttonType = new ButtonType(entry.getValue().get(0).getName() + "  " + entry.getValue().get(1).getName());
            dialogPane.getButtonTypes().add(buttonType);

            // 设置按钮的响应
            dialogPane.lookupButton(buttonType).addEventFilter(
                    javafx.event.ActionEvent.ACTION,
                    event -> {
                        System.out.println("用户选择了吃：" + entry.getKey());
                        List<Item> itemsToEat = entry.getValue();
                        System.out.println("需要吃的牌是：" + itemsToEat.stream().map(Item::getName));
                        // 可以在这里执行具体的吃牌操作
                        // 这里假设有一个方法来处理吃牌逻辑
                        player2.removeMahjong(card);
                        performChiOperation(players.get(1), itemsToEat, card);
                        dialog.close();  // 关闭对话框
                    }
            );
        }

        // 添加取消按钮
        ButtonType cancelButtonType = new ButtonType("取消");
        dialogPane.getButtonTypes().add(cancelButtonType);

        // 添加响应处理程序，处理关闭提示框操作
        dialog.setOnCloseRequest(event -> System.out.println("用户取消了操作"));

        // 显示对话框并等待用户响应
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() != cancelButtonType) {
            return true;
        } else {
            return false;
        }
    }

    private void performChiOperation(Player player, List<Item> itemsToEat, Item card) {
        // 这里应该添加逻辑来从玩家手中移除对应的牌，并将吃进的牌添加到玩家手中
        player.getPlayerMahjong().remove(itemsToEat.get(0));
        player.getPlayerMahjong().remove(itemsToEat.get(1));
        peng.add(itemsToEat.get(0));
        peng.add(itemsToEat.get(1));
        peng.add(card);
        System.out.println("执行了吃牌操作");
    }

    private boolean anhandleGengpen(Player player, Item card) {
        //创建提示窗口
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText("是否需要暗杠牌：" + card.getName());
        ButtonType buttonTypePeng = new ButtonType("an杠");
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypePeng);

        // 添加响应处理程序
        dialog.setOnCloseRequest(event -> {
            // 处理关闭提示框操作
            System.out.println("取消了杠");
        });
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypePeng) {
            System.out.println("用户点击了'杠'按钮");
            //先移除打出玩家的手牌
            List<Item> itemList = players.get(1).getPlayerMahjong();
            // 遍历并删除名称为"Alice"的元素
            int count = 0;
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).getName().equals(card.getName())) {
                    //移除手牌并且添加到碰牌组
                    itemList.remove(i);
                    count++;
                    if (count == 4) {
                        break;
                    }
                }
            }
            peng.add(card);
            //刷新UI
            refreshUI(player, null, card);
            //进入我的回合，并且设置不抽牌
            return true;
        } else {
            return false;
        }
    }

    //ai回合处理
    private void handleAIPlayer(Player player) {
        Platform.runLater(() -> {
            //ai抽牌
            Item drawnCard = drawCard(player);
            //刷新手牌
            refreshUI(player, drawnCard, null);
            //获取手牌随机一张打出
            Random random = new Random();
            Item cardToPlay = player.getPlayerMahjong().get(random.nextInt(player.getPlayerMahjong().size()));
            List<Item> list = new ArrayList<>(players.get(1).getPlayerMahjong());
            list.add(cardToPlay);
            if (isWin(list)) {
                System.out.println("ai给赢了摸到了" + cardToPlay.getName());
                Stage stage = (Stage) borderPane.getScene().getWindow();
                // 创建一个警告对话框
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("恭喜你赢了！");
                alert.setHeaderText("玩家排行榜");
                Label label = new Label("\n张三:   100分\n李四:   90分\n王五:   80分\n赵六:   100分\n田七:   90分\nXX:    80分");
                label.setWrapText(true); // 设置文本自动换行
                alert.getDialogPane().setContent(label);
                alert.getDialogPane().setPrefSize(400, 700); // 设置宽度和高度
                // 添加按钮
                ButtonType buttonTypeOne = new ButtonType("再来一局");
                ButtonType buttonTypeTwo = new ButtonType("退出游戏");
                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

                // 显示对话框并等待响应
                java.util.Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeOne) {
                    //初始化麻将牌组
                    initializeMahjong = null;
                    //初始化玩家组
                    initializePlayer = null;
                    //玩家组
                    players = null;
                    //桌面类
                    table = null;
                    //吃碰杠牌组
                    peng = null;
                    //当前玩家下标
                    currentPlayerIndex = 1;
                    //布局对象
                    BorderPane borderPane = null;
                } else {
                    // 用户选择退出游戏
                    stage.close();
                    System.exit(0);
                    return;
                }
                start(primaryStage);
                System.out.println("再来一局");
                return;
            }

            Map<String, List<Item>> map = canChi(players.get(1), cardToPlay);
            if (player.getName().equals("WEST") && map != null && map.size() > 0) {
                if (handleChi(player, cardToPlay, map)) {
                    currentPlayerIndex = 1;
                    nextTurn(false);
                    return;
                }
            }
            if (canPeng(players.get(1), cardToPlay)) {
                if (handlePeng(player, cardToPlay)) {
                    currentPlayerIndex = 1;
                    nextTurn(false);
                    return;
                }
            }
            if (canGang(players.get(1), cardToPlay)) {
                if (handleGeng(player, cardToPlay)) {
                    currentPlayerIndex = 1;
                    nextTurn(true);
                    return;
                }
            }
            handleTileClick(player, cardToPlay);
        });
    }

    //更新已打出牌区
    private VBox refreshDiscarded(Player player, Item newitem) {
        VBox SOUTHDiscardedTiles = new VBox();
        System.out.println("刷新" + player.getName());
        if (player.getName().equals("NORTH")) {
            borderPane.setTop(null);
            HBox north = createHorizontalPlayerArea(player, 180, newitem);
            VBox NORTHDiscardedTiles = createDiscardedTilesHorizontalnorth(north, table.getDiscardedTiles().get(player.getName()));
            borderPane.setTop(NORTHDiscardedTiles);
        } else if (player.getName().equals("SOUTH")) {
            borderPane.setBottom(null);
            HBox south = createHorizontalPlayerArea(player, 0, newitem);
            SOUTHDiscardedTiles = createDiscardedTilesHorizontalsouth(south, table.getDiscardedTiles().get(player.getName()));
        } else if (player.getName().equals("WEST")) {
            borderPane.setLeft(null);
            VBox east = createVerticalPlayerArea(players.get(2), 90, newitem);
            HBox WESThDiscardedTiles = createDiscardedTilesHorizontalwast(east, table.getDiscardedTiles().get(player.getName()));
            borderPane.setLeft(WESThDiscardedTiles);
        } else if (player.getName().equals("EAST")) {
            borderPane.setRight(null);
            VBox west = createVerticalPlayerArea(players.get(3), -90, newitem);
            HBox EASThDiscardedTiles = createDiscardedTilesHorizontaleast(west, table.getDiscardedTiles().get(player.getName()));
            borderPane.setRight(EASThDiscardedTiles);
        }
        return SOUTHDiscardedTiles;
    }
    //更新UI
    private void refreshUI(Player player, Item newitem, Item penitem) {
        borderPane.setBottom(null);
        VBox SOUTHDiscardedTiles = refreshDiscarded(player, newitem);
        HBox hbox = new HBox(4);
        if (peng != null) {
            for (Item item : peng) {
                ImageView imageView = new ImageView(new Image(String.valueOf(getClass().getResource("/resources/static/" + item.getImgurl()))));
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,51,1), 30, 0, 0, 0);");
                imageView.setFitHeight(45);
                imageView.setFitWidth(30);
                hbox.getChildren().add(imageView);
            }
        }
        hbox.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox(80);
        hbox2.getChildren().add(hbox);
        hbox2.getChildren().add(SOUTHDiscardedTiles);
        hbox2.setAlignment(Pos.CENTER);
        borderPane.setBottom(hbox2);
    }
    // 点击牌处理
    public void handleTileClick(Player player, Item item) {
        System.out.println(player.getName() + "打牌" + item.getName());
        //移除手牌
        player.removeMahjong(item);
        //纪律打出的牌
        table.getDiscardedTiles().get(player.getName()).add(item);
        //更新UI
        refreshUI(player, item, null);
        //下一回合
        proceedToNextTurn(player, item);
    }
    private HBox createHorizontalPlayerArea(Player player, double rotationAngle, Item newitem) {
        HBox hbox = new HBox(3);
        String file = "";

        if (rotationAngle == 0) {
            file = "/resources/static/";
        } else if (rotationAngle == 180) {
            file = "/resources/static/north/";
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
                imageView.setOnMouseClicked(event -> handleTileClick(player, newitem));
                item.setType("old");
            } else {
                imageView.setOnMouseClicked(event -> handleTileClick(player, item));
            }

            imageView.setFitHeight(45);
            imageView.setFitWidth(30);

            hbox.getChildren().add(imageView);
        }

        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    // 辅助方法创建东西左右方向玩家牌区，并应用旋转
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
                imageView.setOnMouseClicked(event -> handleTileClick(player, newitem));
                item.setType("old");
            } else {
                imageView.setOnMouseClicked(event -> handleTileClick(player, item));
            }

            // 设置图像的高度和宽度
            imageView.setFitHeight(30);
            imageView.setFitWidth(45);
            vbox.getChildren().add(imageView);
        }
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    // 创建下南方向打出牌区
    private VBox createDiscardedTilesHorizontalsouth(HBox south, List<Item> discardedTiles) {
        VBox vBox = new VBox(20);
        HBox hbox = new HBox(1); // 设置牌之间的间隔为10
        int count = 0;
        for (Item item : discardedTiles) {
            hbox.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView(new Image(getClass().getResource("/resources/static/" + item.getImgurl()).toString()));
            imageView.setFitHeight(24);
            imageView.setFitWidth(12);
            hbox.getChildren().add(imageView);
            count++;
            if (count % 13 == 0) {
                vBox.getChildren().add(hbox);
                hbox = new HBox(1);// 每13张牌换行
            }
        }
        vBox.getChildren().add(hbox);
        vBox.getChildren().add(south);// 每13张牌换行
        return vBox;
    }

    // 创建上北方向打出牌区
    private VBox createDiscardedTilesHorizontalnorth(HBox north, List<Item> discardedTiles) {
        VBox vBox = new VBox(20);
        HBox hbox = new HBox(1); // 设置牌之间的间隔为10
        vBox.getChildren().add(north);
        int count = 0;
        for (Item item : discardedTiles) {
            hbox.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView(new Image(getClass().getResource("/resources/static/north/" + item.getImgurl()).toString()));
            imageView.setFitHeight(24);
            imageView.setFitWidth(12);
            hbox.getChildren().add(imageView);
            count++;
            if (count % 13 == 0) {
                vBox.getChildren().add(hbox);
                hbox = new HBox(1);// 每13张牌换行
            }
        }
        vBox.getChildren().add(hbox);
        return vBox;
    }

    // 创建右东向打出牌区
    private HBox createDiscardedTilesHorizontaleast(VBox east, List<Item> discardedTiles) {
        HBox hBox = new HBox(20);
        VBox vBox = new VBox(1); // 设置牌之间的间隔为10
        int count = 0;
        for (Item item : discardedTiles) {
            vBox.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView(new Image(getClass().getResource("/resources/static/east/" + item.getImgurl()).toString()));
            imageView.setFitHeight(12);
            imageView.setFitWidth(24);
            vBox.getChildren().add(imageView);
            count++;
            if (count % 13 == 0) {
                hBox.getChildren().add(vBox);
                vBox = new VBox(1);// 每13张牌换行
            }
        }
        hBox.getChildren().add(vBox);
        hBox.getChildren().add(east);// 每13张牌换行
        return hBox;
    }

    // 创建左西方向打出牌区
    private HBox createDiscardedTilesHorizontalwast(VBox wast, List<Item> discardedTiles) {
        HBox hBox = new HBox(20);
        VBox vBox = new VBox(1); // 设置牌之间的间隔为10
        hBox.getChildren().add(wast);// 每13张牌换行
        int count = 0;
        for (Item item : discardedTiles) {
            vBox.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView(new Image(getClass().getResource("/resources/static/west/" + item.getImgurl()).toString()));
            imageView.setFitHeight(12);
            imageView.setFitWidth(24);
            vBox.getChildren().add(imageView);
            count++;
            if (count % 13 == 0) {
                hBox.getChildren().add(vBox);
                vBox = new VBox(1);// 每13张牌换行
            }
        }
        hBox.getChildren().add(vBox);
        return hBox;
    }

    //下一个回合方法
    private void proceedToNextTurn(Player player, Item card) {
        //进行渲染，手牌，废牌，对牌区域
        refreshUI(player, card, null);
        //计算下个玩家回合的下标
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        //进行下一回合
        nextTurn(true);
    }

    public boolean isWin(List<Item> hand) {
        // 空牌组
        int[] a = {};
        a = convertToIntegerArray(hand);
        System.out.println(Arrays.toString(a));
        return isHu(a);
    }

    public static int[] convertToIntegerArray(List<Item> tiles) {

        int[] cardArray = new int[tiles.size()];
        for (int i = 0; i < tiles.size(); i++) {
            cardArray[i] = tiles.get(i).getShu();
        }
        Arrays.sort(cardArray);
        return cardArray;
    }

    //判断手牌是否胡了
    public static boolean isHu(int[] cards) {
        if (null == cards) {
            return false;
        }
        // 胡的牌的个数必须是2或5或8或11或14
        if (cards.length != 2 && cards.length != 5 && cards.length != 8 && cards.length != 11 && cards.length != 14) {
            return false;
        }
        // 将手牌中的将取出来
        int[] js = getJiangs(cards);
        if (null == js || js.length <= 0) {
            return false;
        }

        for (int j : js) {
            int[] tempCards = Arrays.copyOf(cards, cards.length);
            tempCards = removeOne(tempCards, j);
            tempCards = removeOne(tempCards, j);
            Arrays.sort(tempCards);
            // 去掉所有的刻子
            tempCards = removeAllKe(tempCards);
            if (tempCards.length <= 0) {
                return true;
            }

            // 去掉所有的顺子
            tempCards = removeAllShun(tempCards);
            if (tempCards.length <= 0) {
                return true;
            }
        }
        return false;
    }

    //获取牌组中所有的“将”
    private static int[] getJiangs(int[] cards) {
        int[] res = new int[0];
        if (null != cards && cards.length > 1) {
            for (int i = 0; i < cards.length - 1; i++) {
                if (cards[i] == cards[i + 1]) {
                    res = add(res, cards[i]);
                    i++;
                }
            }
        }
        return res;
    }

    //去掉牌组中所有的刻子
    private static int[] removeAllKe(int[] cards) {
        for (int i = 0; i < cards.length - 2; i++) {
            if (cards[i] == cards[i + 1] && cards[i] == cards[i + 2]) {
                cards = removeOne(cards, cards[i]);
                cards = removeOne(cards, cards[i]);
                cards = removeOne(cards, cards[i]);
            }
        }
        return cards;
    }

    //去掉牌组中所有的顺子
    private static int[] removeAllShun(int[] cards) {
        int[] res = Arrays.copyOf(cards, cards.length);
        for (int i = 0; i < cards.length - 2; i++) {
            if (cards[i] + 1 == cards[i + 1] && cards[i + 1] + 1 == cards[i + 2]) {
                res = removeOne(res, cards[i]);
                res = removeOne(res, cards[i + 1]);
                res = removeOne(res, cards[i + 2]);
                i += 2;
            }
        }
        return res;
    }

    //获取去掉花色的牌的值
    private static int getCardWithoutSuit(int card) {
        return card % 10;
    }

    // 将牌card加到牌组cards中
    private static int[] add(int[] cards, int card) {
        int[] res = new int[cards.length + 1];
        System.arraycopy(cards, 0, res, 0, cards.length);
        res[res.length - 1] = card;
        return res;
    }

    // 在牌组中去掉一张牌
    private static int[] removeOne(int[] cards, int card) {
        if (null == cards || cards.length <= 0) {
            return cards;
        }
        Arrays.sort(cards);
        int index = Arrays.binarySearch(cards, card);
        if (index >= 0) {
            int[] res = new int[cards.length - 1];
            int j = 0;
            for (int i = 0; i < cards.length; i++) {
                if (i != index) {
                    res[j++] = cards[i];
                }
            }
            return res;
        }
        return cards;
    }
    //登录界面
    public void login(Stage primaryStage, Runnable onSuccess) {
        primaryStage.setTitle("登录");
        // 创建网格布局
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        // 创建控件
        Label titleLabel = new Label("欢迎登录");
        titleLabel.setFont(Font.font("Arial", 20));
        Label usernameLabel = new Label("用户名:");
        TextField usernameField = new TextField();
        Button loginButton = new Button("登录");
        // 将控件添加到网格布局中
        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(loginButton, 1, 3);
        // 设置登录按钮点击事件
        loginButton.setOnAction(e -> {
            primaryStage.close();
            onSuccess.run(); // 调用成功回调
        });
        Scene scene = new Scene(gridPane, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
