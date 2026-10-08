package ir.maktabsharif.transform;

class ComposedTransformer<I1, O1, O2> implements Transformer<I1, O2> {

    private final Transformer<I1, O1> transformerA;
    private final Transformer<O1, O2> transformerB;

    public ComposedTransformer(Transformer<I1, O1> transformerA, Transformer<O1, O2> transformerB) {
        this.transformerA = transformerA;
        this.transformerB = transformerB;
    }

    @Override
    public O2 transform(I1 i1) {
        O1 o1 = transformerA.transform(i1);
        O2 o2 = transformerB.transform(o1);
        return o2;
    }
}