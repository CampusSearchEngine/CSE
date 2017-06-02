package serv;

import java.util.Vector;

/**
 * Created by THU73 on 17/6/2.
 */
public class CosineUnit {
    private String word;
    private Vector<Double> value;
    private double distance;

    public void setWord(String w) {
        this.word = w;
    }
    public String getWord() {
        return this.word;
    }

    public void setValue(Vector<Double> v) {
        this.value = v;
    }

    public void setDistance(CosineUnit c) {
        distance = cosDistance(this, c);
    }
    public double getDistance() {
        return this.distance;
    }

    static public double cosDistance(CosineUnit c1, CosineUnit c2) {
        double abs1 = 0, abs2 = 0, sum = 0;
        assert (c1.value.size() == 100);
        assert (c2.value.size() == 100);
        for (int i = 0; i < c1.value.size(); ++i) {
            double v1 = c1.value.elementAt(i);
            double v2 = c2.value.elementAt(i);
            sum += v1 * v2;
            abs1 += v1 * v1;
            abs2 += v2 * v2;
        }
        return sum / (Math.sqrt(abs1) * Math.sqrt(abs2));
    }
}
