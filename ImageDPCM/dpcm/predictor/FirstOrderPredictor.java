package dpcm.predictor;

public class FirstOrderPredictor extends Predictor
{
    public Integer predict(Integer a, Integer b, Integer c)
    {
        return a;
    }
    
    public Integer getType()
    {
        return 0;
    }
}