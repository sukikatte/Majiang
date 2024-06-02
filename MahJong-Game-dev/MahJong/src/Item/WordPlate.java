package Item;

//WordPlate entity class
public class WordPlate extends Item{
    int shu;
    public WordPlate(String name,String imgurl,int shu) {

        super("WordPlate", name, imgurl);
        this.shu = shu;
    }
}
