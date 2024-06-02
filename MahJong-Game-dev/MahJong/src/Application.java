
import Game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "resources")
public class Application {

    @Autowired
    private static Game game;
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Application.class, args);
        game = new Game();
        Game.main(args);
    }
}
