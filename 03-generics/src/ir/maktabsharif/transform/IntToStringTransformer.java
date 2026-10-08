package ir.maktabsharif.transform;

public class IntToStringTransformer implements Transformer<Integer, String> {
    @Override
    public String transform(Integer input) {
        return Integer.toString(input);
    }
}
