import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ranakrc on 10-Nov-17.
 */
public class State {

    public int cost = 0;
    public int perFreq = 0;
    public int number = 0;
    public List<Integer>[] outer;

    State() {}

    public void initialize(int periods, int freq) {
        number = periods;
        perFreq = freq;
        outer = new List[number];
        for (int i = 0; i < number; i++) {
            outer[i] = new ArrayList<>();
        }
    }

    public void cpyArr(List<Integer> a, int x) {
        for(int i = 0; i < a.size(); i++)
            outer[x].add(a.get(i));
    }

    public void cpy(List<Integer>[] arr) {
        for(int i = 0; i < number; i++) {
            cpyArr(arr[i], i);
        }
    }
}

