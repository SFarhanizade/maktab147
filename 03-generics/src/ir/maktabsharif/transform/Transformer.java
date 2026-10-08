package ir.maktabsharif.transform;

public interface Transformer<Input,Output> {
    Output transform(Input input);
}
