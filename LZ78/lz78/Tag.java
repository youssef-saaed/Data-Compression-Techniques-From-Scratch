package lz78;

public class Tag {
    private Integer key;
    private char next;

    public Tag(int key, char next)
    {
        this.key = key;
        this.next = next;
    }

    public int getKey()
    {
        return key;
    }

    public char getNext()
    {
        return next;
    }

    public String toString()
    {
        return "<" + key + "," + next + ">";
    }
}
