package dpcm.predictor;

public class SecondOrderPredictor extends Predictor
{
    public Integer predict(Integer a, Integer b, Integer c)
    {
        return a + c - b;
    }

    public Integer getType()
    {
        return 1;
    }
}
