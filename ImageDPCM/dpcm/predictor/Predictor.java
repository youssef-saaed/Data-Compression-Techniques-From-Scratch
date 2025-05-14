package dpcm.predictor;

public abstract class Predictor
{
    public abstract Integer predict(Integer a, Integer b, Integer c);
    public abstract Integer getType();

    public enum PredictorType
    {
        FIRST_ORDER,
        SECOND_ORDER,
        ADAPTIVE
    }
}