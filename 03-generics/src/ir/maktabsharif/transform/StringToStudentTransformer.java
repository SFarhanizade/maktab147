package ir.maktabsharif.transform;

//name,age
public class StringToStudentTransformer implements Transformer<String, Student> {
    @Override
    public Student transform(String input) {
        String[] parts = input.split(",");
        var name = parts[0];
        var age = Integer.parseInt(parts[1]);
        return new Student(name, age);
    }
}
