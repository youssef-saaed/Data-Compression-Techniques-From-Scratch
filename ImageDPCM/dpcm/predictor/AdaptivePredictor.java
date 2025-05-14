package dpcm.predictor;

public class AdaptivePredictor extends Predictor
{
    public Integer predict(Integer a, Integer b, Integer c)
    {
        if (b <= Math.min(a, c))
        {
            return Math.max(a, c);
        }
        if (b >= Math.max(a, c))
        {
            return Math.min(a, c);
        }
        return a + c - b;
    }

    public Integer getType()
    {
        return 2;
    }
}
