package ir.maktabsharif.transform;

public class Main {
    void main() {
        Object input1 = "1";
        Object input2 = 1;

        Transformer<Integer,String> int2Str = new IntToStringTransformer();
        Transformer<String,Student> str2Student = new StringToStudentTransformer();
        Transformer<String,Integer> str2Int = new StringToIntTransformer();

        Transformer<Integer, Integer> composed = new ComposedTransformer<>(int2Str,str2Int);

        var result = transform(1, composed);
        IO.println(result);

//
//        int input = 1;
//        String s1 = transform(input, int2Str);
//        int i1 = transform(s1, str2Int);
//        IO.println(input==i1);

        var student = transform("ali,12", str2Student);
        IO.println(student);

    }


    <Input, Output> Output transform(Input input, Transformer<Input, Output> transformer) {
        return transformer.transform(input);
    }
}
