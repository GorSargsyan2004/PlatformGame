
import utils.LoadSave;
public class TestHurt {
    public static void main(String[] args) throws Exception {
        System.out.println("Playing hurt sound...");
        LoadSave.playSound(LoadSave.SOUND_HURT);
        Thread.sleep(2000);
    }
}
