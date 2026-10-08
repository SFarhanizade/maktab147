package ir.maktabsharif.transform;

public class StringToIntTransformer implements Transformer<String, Integer> {
    @Override
    public Integer transform(String input) {
        return Integer.parseInt(input);
    }
}
