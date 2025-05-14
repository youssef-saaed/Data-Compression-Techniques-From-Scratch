package dpcm;

public class UniformQuantizer {
    private Integer levels;
    private Integer stepSize;
    private Integer start;

    public UniformQuantizer(Integer levels, Integer start, Integer end)
    {
        stepSize = (int)Math.ceil((end - start) / (double)levels);
        this.levels = levels;
        this.start = start;
    }

    public Integer quantize(Integer number)
    {
        Integer q = (number - start) / stepSize;
        q = Math.max(q, 0);
        q = Math.min(q, levels - 1);
        return q;
    }

    public Integer dequantize(Integer number)
    {
        return start + number * stepSize + stepSize / 2;
    }
}
