package ir.maktabsharif.files.todo.dto;

public record CategoryView(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
